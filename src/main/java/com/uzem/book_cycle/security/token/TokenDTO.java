package com.uzem.book_cycle.security.token;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {

    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn; // 액세스 토큰 만료 시간
    private String refreshToken;

    public TokenDTO(String newAccessToken) {
        this.accessToken = newAccessToken;
    }

    public static TokenDTO create(String accessToken, Long accessTokenExpiresIn) {
        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }
}
