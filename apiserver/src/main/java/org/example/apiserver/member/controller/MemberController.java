package org.example.apiserver.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.apiserver.member.dto.MemberCreateRequest;
import org.example.apiserver.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/members")
    public ResponseEntity<Void> createMember(@RequestBody MemberCreateRequest memberCreateRequest) {
        Long memberId = memberService.createMember(memberCreateRequest);
        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

}
