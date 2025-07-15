package oopsops.app.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import oopsops.app.authentication.service.KeycloakService;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class KeycloakServiceTest {

  @InjectMocks
  private KeycloakService svc;

  @Mock
  private RestTemplate restTemplate;

  private final String URL = "https://kc.example.com";
  private final String REALM = "myrealm";
  private final String CLIENT = "client-id";
  private final String SECRET = "client-secret";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(svc, "keycloakUrl", URL);
    ReflectionTestUtils.setField(svc, "realm", REALM);
    ReflectionTestUtils.setField(svc, "clientId", CLIENT);
    ReflectionTestUtils.setField(svc, "clientSecret", SECRET);
    ReflectionTestUtils.setField(svc, "restTemplate", restTemplate);
  }

  @Test
  @DisplayName("loginWithPassword → returns tokens on 200")
  void loginSuccess() {

    Map<String, Object> fakeBody = Map.of("access_token", "A", "refresh_token", "R");
    ResponseEntity<Map> fakeResp = new ResponseEntity<>(fakeBody, HttpStatus.OK);

    given(restTemplate.postForEntity(
        eq(URL + "/realms/" + REALM + "/protocol/openid-connect/token"),
        any(HttpEntity.class),
        eq(Map.class))).willReturn(fakeResp);

    Map<String, Object> tokens = svc.loginWithPassword("u", "p");
    assertThat(tokens)
        .containsEntry("access_token", "A")
        .containsEntry("refresh_token", "R");
  }

  @Test
  @DisplayName("loginWithPassword → throws on non-2xx or null body")
  void loginFailure() {
    ResponseEntity<Map> badResp = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .willReturn(badResp);

    assertThatThrownBy(() -> svc.loginWithPassword("u", "p"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Login failed");
  }

  @Test
  @DisplayName("refreshWithToken → returns tokens on 200")
  void refreshSuccess() {
    Map<String, Object> fakeBody = Map.of("access_token", "A2", "refresh_token", "R2");
    ResponseEntity<Map> fakeResp = new ResponseEntity<>(fakeBody, HttpStatus.OK);

    given(restTemplate.postForEntity(
        eq(URL + "/realms/" + REALM + "/protocol/openid-connect/token"),
        any(HttpEntity.class),
        eq(Map.class))).willReturn(fakeResp);

    Map<String, Object> tokens = svc.refreshWithToken("rt");
    assertThat(tokens)
        .containsEntry("access_token", "A2")
        .containsEntry("refresh_token", "R2");
  }

  @Test
  @DisplayName("refreshWithToken → throws on non-2xx or null body")
  void refreshFailure() {
    ResponseEntity<Map> badResp = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .willReturn(badResp);

    assertThatThrownBy(() -> svc.refreshWithToken("rt"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Refresh failed");
  }

  @Test
  void registerUser_invokesKeycloakCreate_simplified() {

    Keycloak kcMock = mock(Keycloak.class);
    RealmResource realmR = mock(RealmResource.class);
    UsersResource usersR = mock(UsersResource.class);
    given(kcMock.realm(REALM)).willReturn(realmR);
    given(realmR.users()).willReturn(usersR);

    KeycloakBuilder builderMock = mock(
        KeycloakBuilder.class,
        invocation -> {
          if ("build".equals(invocation.getMethod().getName())) {
            return kcMock;
          }
          return invocation.getMock();
        });

    try (MockedStatic<KeycloakBuilder> st = mockStatic(KeycloakBuilder.class)) {
      st.when(KeycloakBuilder::builder).thenReturn(builderMock);

      svc.registerUser("alice", "alice@example.com", "s3cr3t");
    }

    ArgumentCaptor<UserRepresentation> cap = ArgumentCaptor.forClass(UserRepresentation.class);
    then(usersR).should().create(cap.capture());

    UserRepresentation rep = cap.getValue();
    assertThat(rep.getUsername()).isEqualTo("alice");
    assertThat(rep.getEmail()).isEqualTo("alice@example.com");
    assertThat(rep.isEnabled()).isTrue();

    assertThat(rep.getCredentials()).hasSize(1);
    CredentialRepresentation cred = rep.getCredentials().get(0);
    assertThat(cred.getType()).isEqualTo(CredentialRepresentation.PASSWORD);
    assertThat(cred.getValue()).isEqualTo("s3cr3t");
  }

}
