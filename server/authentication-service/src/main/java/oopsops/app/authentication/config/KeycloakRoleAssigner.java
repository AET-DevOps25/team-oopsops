package oopsops.app.authentication.config;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleAssigner {

  private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleAssigner.class);

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.admin.realm:master}")
  private String adminRealm;

  @Value("${keycloak.admin.username}")
  private String adminUser;

  @Value("${keycloak.admin.password}")
  private String adminPass;

  @Value("${keycloak.client-id}")
  private String backendClientId;

  @Value("${keycloak.realm}")
  private String targetRealm;

  private final List<String> rolesToAssign = List.of(
      "manage-users", "view-users", "query-users");

  @PostConstruct
  public void assignServiceAccountRoles() {
    new Thread(this::assignRolesWithRetry).start();
  }

  private void assignRolesWithRetry() {
    int maxRetries = 30;
    int retryInterval = 10000;

    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        logger.info("Attempting to assign service account roles (attempt {}/{})", attempt, maxRetries);
        assignRoles();
        logger.info("✅ Service account roles assigned successfully: {}", rolesToAssign);
        return;
      } catch (Exception e) {
        logger.warn("Failed to assign roles (attempt {}/{}): {}", attempt, maxRetries, e.getMessage());

        if (attempt == maxRetries) {
          logger.error("❌ Failed to assign service account roles after {} attempts", maxRetries, e);
          return;
        }

        try {
          Thread.sleep(retryInterval);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          logger.error("Interrupted while waiting to retry", ie);
          return;
        }
      }
    }
  }

  private void assignRoles() {
    try (Keycloak kc = KeycloakBuilder.builder()
        .serverUrl(authServerUrl)
        .realm(adminRealm)
        .grantType("password")
        .clientId("admin-cli")
        .username(adminUser)
        .password(adminPass)
        .build()) {

      ClientRepresentation backendClient = kc.realm(targetRealm)
          .clients()
          .findByClientId(backendClientId)
          .stream()
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Client not found: " + backendClientId));

      UserRepresentation saUser = kc.realm(targetRealm)
          .clients()
          .get(backendClient.getId())
          .getServiceAccountUser();

      ClientRepresentation rmClient = kc.realm(targetRealm)
          .clients()
          .findByClientId("realm-management")
          .stream()
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("realm-management client not found"));

      List<RoleRepresentation> roles = kc.realm(targetRealm)
          .clients()
          .get(rmClient.getId())
          .roles()
          .list()
          .stream()
          .filter(r -> rolesToAssign.contains(r.getName()))
          .collect(Collectors.toList());

      kc.realm(targetRealm)
          .users()
          .get(saUser.getId())
          .roles()
          .clientLevel(rmClient.getId())
          .add(roles);
    }
  }
}