package oopsops.app.authentication.config;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleAssigner {

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
      "manage-users", "view-users", "query-users"
    );

    @PostConstruct
    public void assignServiceAccountRoles() {
        try (Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(adminRealm)
                .grantType("password")
                .clientId("admin-cli")
                .username(adminUser)
                .password(adminPass)
                .build()) {

            // 1️⃣ Find the backend client
            ClientRepresentation backendClient =
                kc.realm(targetRealm)
                  .clients()
                  .findByClientId(backendClientId)
                  .stream()
                  .findFirst()
                  .orElseThrow(() -> new IllegalStateException("Client not found"));

            // 2️⃣ Get the service-account user for that client
            UserRepresentation saUser =
                kc.realm(targetRealm)
                  .clients()
                  .get(backendClient.getId())
                  .getServiceAccountUser();

            // 3️⃣ Find the realm-management client
            ClientRepresentation rmClient =
                kc.realm(targetRealm)
                  .clients()
                  .findByClientId("realm-management")
                  .stream()
                  .findFirst()
                  .orElseThrow(() -> new IllegalStateException("realm-management client not found"));

            // 4️⃣ Fetch the RoleRepresentations to assign
            List<RoleRepresentation> roles =
                kc.realm(targetRealm)
                  .clients()
                  .get(rmClient.getId())
                  .roles()
                  .list()
                  .stream()
                  .filter(r -> rolesToAssign.contains(r.getName()))
                  .collect(Collectors.toList());

            // 5️⃣ Assign them to the service account
            kc.realm(targetRealm)
              .users()
              .get(saUser.getId())
              .roles()
              .clientLevel(rmClient.getId())
              .add(roles);

            System.out.println("✅ Service account roles assigned: " + rolesToAssign);
        }
    }
}
