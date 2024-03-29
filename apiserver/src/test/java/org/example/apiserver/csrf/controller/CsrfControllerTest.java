package org.example.apiserver.csrf.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CsrfControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void get_token() throws Exception {
        mockMvc.perform(get("/api/csrf-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.csrfToken.token").isString())
                .andExpect(jsonPath("$.csrfToken.parameterName").isString())
                .andExpect(jsonPath("$.csrfToken.headerName").isString());
    }

}
