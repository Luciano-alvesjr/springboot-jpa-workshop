package com.luciano.workshop;

import com.luciano.workshop.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WorkshopApiTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;

    private static final String NEW_USER = """
        {"name":"Bob Brown","email":"bob@gmail.com","phone":"977557755","password":"123456"}
        """;
    private static final String UPDATE_USER = """
        {"name":"Maria Updated","email":"maria.updated@gmail.com","phone":"999999999"}
        """;

    @Test
    void listsSeededUsersWithoutPasswords() throws Exception {
        mvc.perform(get("/users")).andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("Maria Brown"))
            .andExpect(jsonPath("$..password").doesNotExist())
            .andExpect(jsonPath("$..orders").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"users", "products", "categories", "orders"})
    void retrievesResourceById(String resource) throws Exception {
        mvc.perform(get("/" + resource + "/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"users", "products", "categories", "orders"})
    void returnsStructuredNotFound(String resource) throws Exception {
        mvc.perform(get("/" + resource + "/99999")).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.path").value("/" + resource + "/99999"));
    }

    @Test
    void createsRetrievesUpdatesAndDeletesUser() throws Exception {
        var result = mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(NEW_USER))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.password").doesNotExist()).andReturn();
        String location = result.getResponse().getHeader("Location");
        mvc.perform(get(location)).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Bob Brown"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content(UPDATE_USER))
            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Maria Updated"));
        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void hashesNewPasswordsAndNormalizesEmail() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
            .content(NEW_USER.replace("bob@gmail.com", "BOB@GMAIL.COM")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("bob@gmail.com"));
        var user = users.findAll().stream().filter(u -> u.getEmail().equals("bob@gmail.com")).findFirst().orElseThrow();
        assertThat(user.getPassword()).isNotEqualTo("123456");
        assertThat(passwords.matches("123456", user.getPassword())).isTrue();
    }

    @Test
    void updatePreservesPasswordAndOrders() throws Exception {
        String original = users.findById(1L).orElseThrow().getPassword();
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(UPDATE_USER))
            .andExpect(status().isOk());
        var updated = users.findById(1L).orElseThrow();
        assertThat(updated.getPassword()).isEqualTo(original);
        assertThat(updated.getOrders()).hasSize(2);
    }

    @Test
    void duplicateEmailReturnsConflict() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
            .content(NEW_USER.replace("bob@gmail.com", "MARIA@GMAIL.COM")))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void updateToExistingEmailReturnsConflict() throws Exception {
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_USER.replace("maria.updated@gmail.com", "alex@gmail.com")))
            .andExpect(status().isConflict());
    }

    @Test
    void rejectsMissingAndInvalidFields() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\" \",\"email\":\"invalid\",\"phone\":\"\",\"password\":\"123\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors", hasSize(4)))
            .andExpect(jsonPath("$.message").value("Invalid request fields"));
    }

    @Test
    void rejectsInvalidUpdate() throws Exception {
        mvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    void rejectsPasswordsExceedingBcryptByteLimit() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
            .content(NEW_USER.replace("123456", "é".repeat(40))))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1"})
    void rejectsInvalidIds(String id) throws Exception {
        mvc.perform(get("/users/" + id)).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void cannotDeleteUserWithOrders() throws Exception {
        mvc.perform(delete("/users/1")).andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message", not(containsString("SQL"))));
    }

    @Test
    void missingUpdateReturnsNotFound() throws Exception {
        mvc.perform(put("/users/99999").contentType(MediaType.APPLICATION_JSON).content(UPDATE_USER))
            .andExpect(status().isNotFound());
    }

    @Test
    void missingDeleteReturnsNotFound() throws Exception {
        mvc.perform(delete("/users/99999")).andExpect(status().isNotFound());
    }

    @Test
    void listsCatalogWithManyToManyCategories() throws Exception {
        mvc.perform(get("/products")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5)));
        mvc.perform(get("/products/3")).andExpect(status().isOk())
            .andExpect(jsonPath("$.categories", hasSize(2)))
            .andExpect(jsonPath("$.categories[*].name", contains("Electronics", "Computers")));
        mvc.perform(get("/categories")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void returnsOrderItemsSharedIdPaymentAndTotal() throws Exception {
        mvc.perform(get("/orders/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.orderStatus").value("PAID"))
            .andExpect(jsonPath("$.moment").value("2019-06-20T19:53:07Z"))
            .andExpect(jsonPath("$.client.name").value("Maria Brown"))
            .andExpect(jsonPath("$.items", hasSize(2)))
            .andExpect(jsonPath("$.items[0].subTotal").value(181.00))
            .andExpect(jsonPath("$.payment.id").value(1))
            .andExpect(jsonPath("$.payment.moment").value("2019-06-20T21:53:07Z"))
            .andExpect(jsonPath("$.total").value(1431.00))
            .andExpect(jsonPath("$..password").doesNotExist());
    }

    @Test
    void supportsUnpaidOrdersAndExactDecimals() throws Exception {
        mvc.perform(get("/orders")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
        mvc.perform(get("/orders/2")).andExpect(jsonPath("$.total").value(2500.00))
            .andExpect(jsonPath("$.payment").value(nullValue()));
        mvc.perform(get("/orders/3")).andExpect(jsonPath("$.total").value(201.98))
            .andExpect(jsonPath("$.orderStatus").value("WAITING_PAYMENT"));
    }
}
