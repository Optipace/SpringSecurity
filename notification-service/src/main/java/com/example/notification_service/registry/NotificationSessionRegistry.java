package com.example.notification_service.registry;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSessionRegistry {
    private final ConcurrentHashMap<String,String> userToSessionId=new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,String> sessionIdToUser=new ConcurrentHashMap<>();
    public void register(String username,String sessionId){
        userToSessionId.put(username,sessionId);
        sessionIdToUser.put(sessionId,username);
    }

    public void unregisterBySessionId(String sessionId){
        String username=sessionIdToUser.remove(sessionId);
        if(username!=null){
            userToSessionId.remove(username);
        }
    }

    public boolean isOnline(String username){
        return userToSessionId.containsKey(username);
    }

    public String getSessionId(String username){
        return userToSessionId.get(username);
    }
}
