package com.example.notification_service.config;

import com.example.notification_service.security.JWTUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JWTUtil jwtUtil;
//    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker("/topic", "/queue");
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println("inside websocket interceptor");
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
                    List<String> authorization=accessor.getNativeHeader("Authorization");
                    if(authorization == null || authorization.isEmpty()){
                        throw new MessageDeliveryException("Missing Authorization Header");
                    }
                    String bearerToken = authorization.get(0);
                    if(!bearerToken.startsWith("Bearer ")) {
                        throw new MessageDeliveryException("Invalid token format");
                    }
                    String token=bearerToken.substring(7);
                    try{
//                        System.out.println("token: "+token);
//                        UserDetails userDetails=customUserDetailsService.loadUserByUsername(jwtUtil.extractUsername(token));
//                        System.out.println("user loaded");
//                        jwtUtil.validateToken(token,userDetails);
//                        String username = jwtUtil.extractUsername(token);
//                        System.out.println("username"+username);
//                        accessor.setUser(new Principal() {
//                            @Override
//                            public String getName() {
//                                return username;
//                            }
                        if(!jwtUtil.validateToken(token)){
                            throw new MessageDeliveryException("Invalid JWT Token");
                        }
                        Long userId= jwtUtil.extractUserId(token);
                        System.out.println("User Id: "+userId);
                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return String.valueOf(userId);
                            }
                        });
                    }catch (Exception exception){
                        exception.printStackTrace();
                        throw new MessageDeliveryException("Invalid token.");
                    }
                }
                return message;
            }
        });
    }
}
