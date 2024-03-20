package org.example.apiserver.member.service;

import lombok.RequiredArgsConstructor;
import org.example.apiserver.member.domain.Member;
import org.example.apiserver.member.dto.MemberCreateRequest;
import org.example.apiserver.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createMember(MemberCreateRequest memberCreateRequest) {
        Member member = memberCreateRequest.toEntity(passwordEncoder);
        return memberRepository.save(member).getId();
    }

}
