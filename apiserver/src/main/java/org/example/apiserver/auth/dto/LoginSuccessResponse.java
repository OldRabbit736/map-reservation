package org.example.apiserver.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record LoginSuccessResponse(String username, Collection<String> authorities) {

    public static LoginSuccessResponse from(User user) {
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return new LoginSuccessResponse(user.getUsername(), authorities);
    }

}
