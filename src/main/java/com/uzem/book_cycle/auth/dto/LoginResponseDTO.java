package com.uzem.book_cycle.auth.dto;

import com.uzem.book_cycle.security.token.TokenDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;

    public static LoginResponseDTO create(TokenDTO tokenDTO) {
        return LoginResponseDTO.builder()
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .build();
    }


}
