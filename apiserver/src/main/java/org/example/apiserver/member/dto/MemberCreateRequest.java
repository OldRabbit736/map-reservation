package org.example.apiserver.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.apiserver.member.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)    // 컨트롤러 Argument 생성 시 Jackson이 이용할 생성자
public class MemberCreateRequest {

    private String email;
    private String password;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        String encodedPassword = passwordEncoder.encode(password);
        return new Member(email, encodedPassword);
    }

    public MemberCreateRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
