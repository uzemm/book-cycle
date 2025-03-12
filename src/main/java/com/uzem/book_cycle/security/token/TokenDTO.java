package com.uzem.book_cycle.security.token;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {

    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn; // 액세스 토큰 만료 시간
    private String refreshToken;

}
