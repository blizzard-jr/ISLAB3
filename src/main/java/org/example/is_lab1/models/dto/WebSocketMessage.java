package org.example.is_lab1.models.dto;

public record WebSocketMessage(
        String action,
        Integer entityId,
        Object payload
) {
    public static WebSocketMessage created(Integer entityId, Object payload) {
        return new WebSocketMessage("CREATED", entityId, payload);
    }

    public static WebSocketMessage updated(Integer entityId, Object payload) {
        return new WebSocketMessage("UPDATED", entityId, payload);
    }

    public static WebSocketMessage deleted(Integer entityId) {
        return new WebSocketMessage("DELETED", entityId, null);
    }
}

