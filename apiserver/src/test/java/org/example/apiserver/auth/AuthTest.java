package org.example.apiserver.auth;

import org.example.apiserver.member.dto.MemberCreateRequest;
import org.example.apiserver.member.repository.MemberRepository;
import org.example.apiserver.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void clean() {
        memberRepository.deleteAll();
    }

    @DisplayName("올바른 이메일과 비밀번호 제공 시 로그인 성공")
    @Test
    void login_success() throws Exception {
        // given
        String email = "abc@gmail.com";
        String password = "12345678";
        memberService.createMember(new MemberCreateRequest(email, password));

        // when, then
        performLogin(email, password)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username").value(email))
                .andExpect(jsonPath("authorities[0]").value("ROLE_MEMBER"));
    }

    @DisplayName("")
    @Test
    void login_failed() throws Exception {
        // given
        String email = "abc@gmail.com";
        String password = "12345678";
        memberService.createMember(new MemberCreateRequest(email, password));

        // when, then
        String wrongPassword = password + "abc";
        performLogin(email, wrongPassword)
                .andDo(print());
        // TODO: Error 응답 모양 정해지면 완성시키기.
    }

    private ResultActions performLogin(String email, String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", email)
                .param("password", password)
                .with(csrf())
        );
    }

}
