package com.uzem.book_cycle.security.token;

import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.security.token.TokenErrorCode.*;

@Slf4j
@Component
public class TokenProvider { // 토큰 생성, 검증, 사용자 정보 추출

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toMillis(); // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(14).toMillis(); // 2주

    private final Key key;
    private final RedisUtil redisUtil;

    public TokenProvider(@Value("${custom.jwt.secret}") String secretKey, RedisUtil redisUtil) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisUtil = redisUtil;
    }

    public TokenDTO generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = generateAccessToken(authentication.getName(), authorities);
        String refreshToken = generateRefreshToken(authentication.getName(), authorities);

        long now = (new Date()).getTime();

        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(new Date(now + ACCESS_TOKEN_EXPIRE_TIME).getTime())
                .refreshToken(refreshToken)
                .build();
    }

    //accessToken 재발급
    public TokenDTO reissueAccessToken(String refreshToken) {
        // Refresh Token 검증 및 클레임에서 Refresh Token 여부 확인
        if (!validateToken(refreshToken)) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰에서 사용자 정보 추출 -> 클레임 확인
        // 클레임(사용자 정보, 만료 시간 등)을 저장하는 객체.
        Claims claims = parseClaims(refreshToken);

        // 유효한 리프레시 토큰인지 확인
        if (!Boolean.TRUE.equals(claims.get("isRefreshToken"))) {
            throw new TokenException(NOT_A_REFRESH_TOKEN);
        }

        String email = claims.getSubject();
        String authorities = claims.get(AUTHORITIES_KEY).toString();

        String newAccessToken = generateAccessToken(email, authorities);
        long accessTokenExpiresIn = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME;

        return TokenDTO.builder()
                .grantType(BEARER_TYPE)
                .accessToken(newAccessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * AccessToken 생성
     */
    private String generateAccessToken(String email, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * RefreshToken 생성
     */
    private String generateRefreshToken(String email, String authorities) {
        long now = (new Date()).getTime();
        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .claim("isRefreshToken", true) // refreshToken 임을 나타내는 클레임 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * JWT 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new TokenException(REFRESH_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new TokenException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * JWT Claims 파싱
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * JWT Claims 파싱 (유효한 토큰만 반환)
     */
    public Claims getClaimsFromValidToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new TokenException(TokenErrorCode.INVALID_REFRESH_TOKEN);
        }

        return parseClaims(refreshToken);
    }

    //남은 accessToken 만료시간
    public long getExpiration(String accessToken) {
        try {
            // JWT 토큰에서 Claims 추출
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            // 만료 시간(exp) 가져오기
            Date expiration = claims.getExpiration();

            // 현재 시간과 비교하여 남은 시간 계산 (밀리초 단위 반환)
            long remainingTime = expiration.getTime() - System.currentTimeMillis();

            return Math.max(remainingTime, 0);
        } catch (Exception e) {
            // 예외 발생 시 0 반환 (잘못된 토큰일 가능성)
            return 0;
        }
    }
}
