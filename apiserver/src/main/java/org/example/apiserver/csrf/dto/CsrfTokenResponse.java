package org.example.apiserver.csrf.dto;

import org.springframework.security.web.csrf.CsrfToken;

public record CsrfTokenResponse(CsrfToken csrfToken) {
}
