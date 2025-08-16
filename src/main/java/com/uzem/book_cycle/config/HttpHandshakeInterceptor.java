package com.uzem.book_cycle.config;

import com.uzem.book_cycle.security.CustomUserDetails;
import com.uzem.book_cycle.security.token.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String accessToken = servletRequest.getParameter("access_token");

        if(accessToken == null || accessToken.isEmpty()){
            return false;
        }

        try{
            // 1. 토큰 검증 및 인증 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(accessToken);

            // 2. SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 선택적으로 사용자 정보도 attributes에 저장 가능
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            attributes.put("memberId", userDetails.getId());

        } catch (Exception e) {
            // 유효하지 않은 토큰이면 연결 거부
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
