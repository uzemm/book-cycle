package com.uzem.book_cycle.security.jwt;

import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.token.TokenProvider;
import com.uzem.book_cycle.security.token.TokenErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        // 요청 URI 가져오기
        String requestURI = request.getRequestURI();

        // JWT 검증을 하지 않고 바로 다음 필터로 넘김
        if (requestURI.startsWith("/auth/signup") || requestURI.startsWith("/auth/login")
                || requestURI.startsWith("/auth/verify-check")
                || requestURI.startsWith("/auth/refresh")) {
            log.debug("✅ 인증 예외 경로 요청: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header 에서 JWT 토큰 추출
        String jwt = tokenProvider.resolveToken(request);

        try {
            // 2. JWT 토큰이 존재하고 유효한 경우
            if (tokenProvider.validateToken(jwt)) {
                //블랙리스트에 있는 토큰인지 확인
                if (isBlackList(jwt)) {
                    throw new TokenException(TokenErrorCode.TOKEN_ALREADY_LOGGED_OUT);
                }
                // 토큰이 유효하면 인증 객체 설정
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT 인증 성공: {}", authentication.getName());
            }
        } catch (TokenException e) {
            log.debug("JWT 검증 실패: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 블랙리스트에 포함된 토큰인지 확인
     */
    public boolean isBlackList(String accessToken) {
        return redisUtil.exists(BLACKLIST_PREFIX  + accessToken);
    }

}
