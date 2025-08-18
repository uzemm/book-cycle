package com.uzem.book_cycle.security.token;

import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_AUTHORITY_NOT_FOUND;
import static com.uzem.book_cycle.security.token.TokenErrorCode.*;

@Slf4j
@Component
public class TokenProvider { // 토큰 생성, 검증, 사용자 정보 추출

    private static final String AUTHORITIES_KEY = "auth";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toMillis(); // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofMinutes(14).toMillis(); // 2주

    private final SecretKey key;
    private final RedisUtil redisUtil;
    private final UserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    public TokenProvider(@Value("${custom.jwt.secret}") String secretKey, RedisUtil redisUtil, UserDetailsService userDetailsService, MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisUtil = redisUtil;
        this.userDetailsService = userDetailsService;
        this.memberRepository = memberRepository;
    }

    public TokenDTO generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = generateAccessToken(Long.valueOf(authentication.getName()), authorities);
        String refreshToken = generateRefreshToken(Long.valueOf(authentication.getName()), authorities);

        long now = (new Date()).getTime();

        return TokenDTO.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(new Date(now + ACCESS_TOKEN_EXPIRE_TIME).getTime())
                .refreshToken(refreshToken)
                .build();
    }

    //accessToken 재발급
    public TokenDTO reissueAccessToken(String refreshToken) {
        // Refresh Token 검증 및 클레임에서 Refresh Token 여부 확인
        if (!validateToken(refreshToken)) {
            throw new TokenException(INVALID_TOKEN);
        }

        // 리프레시 토큰에서 사용자 정보 추출 -> 클레임 확인
        // 클레임(사용자 정보, 만료 시간 등)을 저장하는 객체.
        Claims claims = parseClaims(refreshToken);

        // 유효한 리프레시 토큰인지 확인
        if (!Boolean.TRUE.equals(claims.get("isRefreshToken"))) {
            throw new TokenException(NOT_A_REFRESH_TOKEN);
        }

        Long memberId = Long.valueOf(claims.getSubject());
        String authorities = claims.get(AUTHORITIES_KEY).toString();

        String newAccessToken = generateAccessToken(memberId, authorities);
        long accessTokenExpiresIn = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME;

        return TokenDTO.create(newAccessToken, accessTokenExpiresIn);
    }

    /**
     * AccessToken 생성
     */
    private String generateAccessToken(Long memberId, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .subject(memberId.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(accessTokenExpiresIn)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * RefreshToken 생성
     */
    private String generateRefreshToken(Long memberId, String authorities) {
        long now = (new Date()).getTime();
        return Jwts.builder()
                .subject(memberId.toString())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .claim("isRefreshToken", true) // refreshToken 임을 나타내는 클레임 추가
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        Long memberId = Long.valueOf(claims.getSubject());
        log.info("Extracted userEmail from token: {}", memberId);

        String authString  = claims.get(AUTHORITIES_KEY, String.class);
        if (authString  == null || authString.isBlank()) {
            throw new MemberException(MEMBER_AUTHORITY_NOT_FOUND);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authString.split(","))
                        .filter(auth -> !auth.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        // UserDetailsService 통해 DB 조회
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // CustomUserDetails 사용하도록 변경
        CustomUserDetails userDetails = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /**
     * JWT 유효성 검사
     */
    public boolean validateToken(String token) {
        //null 체크 추가
        if(token == null || token.isBlank()) {
            throw new TokenException(ILLEGAL_TOKEN);
        }
        try {
            Jwts.parser()
                    .verifyWith(key)  // 서명 검증 키 지정
                    .build()
                    .parseSignedClaims(token)
                    .getPayload(); // Claims 부분 추출
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new TokenException(INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new TokenException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new TokenException(ILLEGAL_TOKEN);
        }
    }

    /**
     * JWT Claims 파싱
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)  // 서명 검증 키 지정
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload(); // Claims 부분 추출
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * JWT Claims 파싱 (유효한 토큰만 반환)
     */
    public Claims getClaimsFromValidToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new TokenException(INVALID_TOKEN);
        }

        return parseClaims(refreshToken);
    }

    //남은 accessToken 만료시간
    public long getExpiration(String accessToken) {
        try {
            // JWT 토큰에서 Claims 추출
            Claims claims = Jwts.parser()
                    .verifyWith(key)  // 서명 검증 키 지정
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload(); // Claims 부분 추출


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

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("🔍 Authorization 헤더 값: {}", bearerToken);
        //요청 헤더에서 Authorization: Bearer <JWT> 형식 JWT 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()); // 유지보수성을 위해 7 대신 BEARER_PREFIX.length() 사용
        }
        return null;
    }

    /**
     * accessToken으로 memberId 추출하는 메서드
     */
    public Long getMemberIdFromAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken); // 내부에서 private 메서드 사용
        return Long.valueOf(claims.getSubject());
    }

    public RedisUtil getRedisUtil() {
        return redisUtil;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }
}
