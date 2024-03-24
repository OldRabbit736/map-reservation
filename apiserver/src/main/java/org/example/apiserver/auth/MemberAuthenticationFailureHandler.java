package org.example.apiserver.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.apiserver.exception.domain.ErrorCode;
import org.example.apiserver.exception.dto.CustomErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Member 로그인 실패 시 CustomErrorResponse 형식으로 응답 보내기 위한 클래스
 */
@RequiredArgsConstructor
@Component
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.AUTH_INVALID_USERNAME_OR_PASSWORD;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // 한글 응답이 포함되어 있으므로 UTF-8로 인코딩

        CustomErrorResponse<?> errorResponse = CustomErrorResponse.from(errorCode, null);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

}
