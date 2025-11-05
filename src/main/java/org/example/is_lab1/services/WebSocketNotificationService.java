package org.example.is_lab1.services;

import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyCreated(Integer entityId, Object payload) {
        WebSocketMessage message = WebSocketMessage.created(entityId, payload);
        messagingTemplate.convertAndSend("/topic/entities", message);
    }

    public void notifyUpdated(Integer entityId, Object payload) {
        WebSocketMessage message = WebSocketMessage.updated(entityId, payload);
        messagingTemplate.convertAndSend("/topic/entities", message);
    }

    public void notifyDeleted(Integer entityId) {
        WebSocketMessage message = WebSocketMessage.deleted(entityId);
        messagingTemplate.convertAndSend("/topic/entities", message);
    }

}





