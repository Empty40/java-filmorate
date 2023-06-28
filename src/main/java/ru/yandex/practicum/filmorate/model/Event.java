package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Event {
    private int eventId;
    private Long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;

    //Конструктор для события с созданием времени
    public Event(String operation, String entity, int userId, int entityId) {
        this.timestamp = Instant.now().toEpochMilli();
        this.operation = operation;
        this.eventType = entity;
        this.entityId = entityId;
        this.userId = userId;
    }
}
