package oopsops.app.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import oopsops.app.authentication.controller.AuthController;
import oopsops.app.authentication.controller.GlobalExceptionHandler;
import oopsops.app.authentication.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  void registerSuccess() throws Exception {
    var req = Map.of(
      "username", "bob",
      "email",    "bob@example.com",
      "password", "pw"
    );

    mockMvc.perform(post("/api/v1/authentication/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req))
      )
      .andExpect(status().isOk())
      .andExpect(content().string("User registered successfully!"));

    then(userService).should().registerUser("bob", "bob@example.com", "pw");
  }

//   @Test
//   void registerConflict() throws Exception {
//     willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "exists"))
//       .given(userService).registerUser(any(), any(), any());

//     mockMvc.perform(post("/api/v1/authentication/register")
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(objectMapper.writeValueAsString(Map.of(
//           "username", "x",
//           "email",    "y",
//           "password", "z"
//         )))
//       )
//       .andExpect(status().isConflict())
//       .andExpect(jsonPath("$.message").value("exists"));
//   }

  @Test
  void loginSuccess() throws Exception {
    given(userService.loginWithPassword("u", "p"))
      .willReturn(Map.of("access_token", "A", "refresh_token", "R"));

    mockMvc.perform(post("/api/v1/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "username", "u",
          "password", "p"
        )))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.access_token").value("A"))
      .andExpect(jsonPath("$.refresh_token").value("R"));
  }

  @Test
  void loginNotFound() throws Exception {
    willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"))
      .given(userService).loginWithPassword(any(), any());

    mockMvc.perform(post("/api/v1/authentication/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"x\",\"password\":\"y\"}")
      )
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("not found"));
  }

  @Test
  void refreshSuccess() throws Exception {
    given(userService.refreshWithToken("rt"))
      .willReturn(Map.of("access_token", "A2", "refresh_token", "R2"));

    mockMvc.perform(post("/api/v1/authentication/refresh")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"refresh_token\":\"rt\"}")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.access_token").value("A2"))
      .andExpect(jsonPath("$.refresh_token").value("R2"));
  }

  @Test
  void refreshMissing() throws Exception {
    mockMvc.perform(post("/api/v1/authentication/refresh")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}")
      )
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("Refresh token is required"));
  }
}