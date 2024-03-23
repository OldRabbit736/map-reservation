package org.example.apiserver.auth;

import lombok.RequiredArgsConstructor;
import org.example.apiserver.member.domain.Member;
import org.example.apiserver.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Member 로그인 시 사용될 UserDetailsService 정의
 */
@RequiredArgsConstructor
@Component
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("username: " + username));
        return new User(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }

}
