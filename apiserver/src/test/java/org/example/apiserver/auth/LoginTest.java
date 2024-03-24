package org.example.apiserver.auth;

import org.assertj.core.api.Assertions;
import org.example.apiserver.exception.domain.ErrorCode;
import org.example.apiserver.member.domain.Member;
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

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class LoginTest {

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

    @DisplayName("비밀번호가 일치하지 않으면 로그인 실패 응답을 내보낸다.")
    @Test
    void login_failed_invalid_password() throws Exception {
        // given
        String email = "abc@gmail.com";
        String password = "12345678";
        memberService.createMember(new MemberCreateRequest(email, password));

        // when, then
        String wrongPassword = password + "zxc";
        performLogin(email, wrongPassword)
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @DisplayName("존재하지 않는 유저라면 로그인 실패 응답을 내보낸다.")
    @Test
    void login_failed_not_existing_user() throws Exception {
        // given
        String email = "abc@gmail.com";
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Assertions.assertThat(optionalMember).isEmpty();

        // when, then
        String password = "12345678";
        performLogin(email, password)
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    private ResultActions performLogin(String email, String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .param("username", email)
                .param("password", password)
                .with(csrf())
        );
    }

}
