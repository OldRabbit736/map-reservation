package org.example.apiserver.member.service;

import org.example.apiserver.member.domain.Member;
import org.example.apiserver.member.dto.MemberCreateRequest;
import org.example.apiserver.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void createMember() {
        // given
        String email = "abc@gmail.com";
        String password = "12345678";
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest(email, password);

        // when
        Long memberId = memberService.createMember(memberCreateRequest);

        // then
        Optional<Member> foundMember = memberRepository.findById(memberId);
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo(email);
    }

}
