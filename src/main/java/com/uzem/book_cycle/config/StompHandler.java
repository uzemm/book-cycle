package com.uzem.book_cycle.config;

import com.uzem.book_cycle.exception.TokenException;
import com.uzem.book_cycle.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


import static com.uzem.book_cycle.security.token.TokenErrorCode.INVALID_TOKEN;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1. 토큰 꺼내기
            String accessToken = (String) accessor.getSessionAttributes().get("token");

            // 2. 유효성 검사
            if(!tokenProvider.validateToken(accessToken)) {
                throw new TokenException(INVALID_TOKEN);
            }

            // 3. 유저 ID 추출
            Long memberId = tokenProvider.getMemberIdFromAccessToken(accessToken);

            // 4. Principal 주입
            accessor.setUser(new StompPrincipal(memberId.toString()));
        }
        return message;
    }
}
