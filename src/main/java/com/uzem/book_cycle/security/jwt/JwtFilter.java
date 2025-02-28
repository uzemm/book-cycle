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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "BLACKLIST:";

    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 JWT 토큰 추출
        String jwt = resolveToken(request);

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
                log.info("✅ JWT 인증 성공: {}", authentication.getName());
            }
        } catch (TokenException e) {
            log.warn("❌ JWT 검증 실패: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        //요청 헤더에서 Authorization: Bearer <JWT> 형식 JWT 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()); // ✅ 유지보수성을 위해 7 대신 BEARER_PREFIX.length() 사용
        }
        return null;
    }

    /**
     * 블랙리스트에 포함된 토큰인지 확인
     */
    public boolean isBlackList(String accessToken) {
        return redisUtil.exists(BLACKLIST_PREFIX  + accessToken);
    }
}
