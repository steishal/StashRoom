package org.example.stashroom.utils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AuthTokenProvider tokenProvider;

    @Autowired
    public WebSocketAuthInterceptor(AuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (tokenProvider.validateToken(token)) {
                    Authentication auth = tokenProvider.getAuthentication(token);
                    accessor.setUser(auth);
                }
            }
        }

        return message;
    }
}
