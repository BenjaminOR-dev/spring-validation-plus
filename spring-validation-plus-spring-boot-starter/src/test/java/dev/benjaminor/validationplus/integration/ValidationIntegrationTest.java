package dev.benjaminor.validationplus.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
class ValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnValidationErrorsForInvalidRequestBody() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "email": "invalid",
                                  "size": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name[0]").value("El campo name es obligatorio."))
                .andExpect(jsonPath("$.errors.email[0]").value(containsString("correo")));
    }

    @Test
    void shouldReturnFriendlyIntegerConversionError() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("size", "abc")
                        .header("Accept-Language", "es"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size[0]").value("El campo size debe ser un entero."));
    }

    @Test
    void shouldReturnRequiredIfError() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "name": "Benjamin",
                                  "email": "user@example.com",
                                  "size": 1,
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.adminCode[0]").exists());
    }

    @Test
    void shouldReturnFriendlyIntegerConversionErrorInJsonBody() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "name": "Benjamin",
                                  "email": "user@example.com",
                                  "size": "abc"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size[0]").value("El campo size debe ser un entero."));
    }

    @Test
    void shouldReturnFriendlyBooleanConversionError() throws Exception {
        mockMvc.perform(get("/api/users/flags")
                        .param("active", "maybe")
                        .header("Accept-Language", "es"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.active[0]").value("El campo active debe ser verdadero o falso."));
    }

    @Test
    void shouldReturnFriendlyDecimalConversionErrorInJsonBody() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "name": "Benjamin",
                                  "email": "user@example.com",
                                  "size": 1,
                                  "score": "not-a-number"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.score[0]").value("El campo score debe ser un número decimal."));
    }

    @Test
    void shouldReturnMalformedJsonError() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "name": "Benjamin"
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.body[0]").value("El cuerpo de la solicitud contiene JSON malformado."));
    }

    @Test
    void shouldReturnConfirmedError() throws Exception {
        mockMvc.perform(post("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "es")
                        .content("""
                                {
                                  "password": "secret",
                                  "passwordConfirmation": "other"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.passwordConfirmation[0]").value(containsString("confirmación")));
    }

    @Test
    void shouldReturnFriendlyIntegerConversionErrorForModelAttribute() throws Exception {
        mockMvc.perform(post("/api/users/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Benjamin")
                        .param("email", "user@example.com")
                        .param("size", "abc")
                        .header("Accept-Language", "es"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size[0]").value("El campo size debe ser un entero."));
    }

    @Test
    void shouldReturnValidationErrorForInvalidPathVariable() throws Exception {
        mockMvc.perform(get("/api/users/0")
                        .header("Accept-Language", "es"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id[0]").exists());
    }
}
