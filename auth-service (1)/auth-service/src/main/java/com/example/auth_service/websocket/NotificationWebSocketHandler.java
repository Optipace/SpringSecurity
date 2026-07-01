package com.example.auth_service.websocket;

import com.example.auth_service.dto.NotificationMessage;
import com.example.auth_service.dto.RegisterMessage;
import com.example.auth_service.dto.RegistrationResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.CustomUserDetailsService;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTUtil jwtUtil;
    private final Map<Long,WebSocketSession> userSessions=new ConcurrentHashMap<>();
    private final Map<String,Long> sessionToUser=new ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception{
        System.out.println("client connected: "+webSocketSession.getId());
        webSocketSession.sendMessage(new TextMessage("Connected successfully"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession,TextMessage textMessage) throws Exception{
        System.out.println("received"+textMessage.getPayload());
        ObjectMapper clientMessage=new ObjectMapper();
        RegisterMessage registerMessage=clientMessage.readValue(textMessage.getPayload(),RegisterMessage.class);
        String type=registerMessage.getType();
        if("REGISTER".equalsIgnoreCase(type)) {
            String token = registerMessage.getToken();
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                System.out.println("Token valid");
                User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
                Long userId = user.getId();
                userSessions.put(userId, webSocketSession);
                System.out.println("User" + userId + "registered for web socket notifications");
                ObjectMapper backendMessage = new ObjectMapper();
                RegistrationResponse registrationResponse = new RegistrationResponse("REGISTERED");
                webSocketSession.sendMessage(new TextMessage(backendMessage.writeValueAsString(registrationResponse)));
            }
            else{
                System.out.println("Invalid token");
                webSocketSession.close(CloseStatus.POLICY_VIOLATION.withReason("Invalid JWT token"));
            }
        }
           else if("PING".equals(registerMessage.getType())){
                ObjectMapper heartbeatMessage=new ObjectMapper();
                Map<String,String> response=new HashMap<>();
                response.put("type","PONG");
                webSocketSession.sendMessage(new TextMessage(heartbeatMessage.writeValueAsString(response)));
            }
           else{
               System.out.println("Unknown message type"+type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession,org.springframework.web.socket.CloseStatus status) throws Exception{
        Long userId=sessionToUser.remove(webSocketSession.getId());
        if(userId!=null){
            userSessions.remove(userId);
            System.out.println("User"+userId+"disconnected");
        }
        System.out.println("Connection closed");
    }

    public void sendNotifications(NotificationMessage notificationMessage) {
        try{
            Long userId=notificationMessage.getUserId();
            WebSocketSession webSocketSession=userSessions.get(userId);
            if(webSocketSession!=null && webSocketSession.isOpen()){
                ObjectMapper notificationResponse=new ObjectMapper();
                String json=notificationResponse.writeValueAsString(notificationMessage);
                webSocketSession.sendMessage(new TextMessage(json));
                System.out.println("Notification sent to user: "+userId);
            }
            else{
                System.out.println("User "+userId+"is not connected");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
