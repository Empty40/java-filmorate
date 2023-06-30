package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.Entity;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Event {
    private int eventId;
    private Long timestamp;
    private int userId;
    private Entity eventType;
    private Operation operation;
    private int entityId;

    //Конструктор для события с созданием времени
    public Event(Operation operation, Entity eventType, int userId, int entityId) {
        this.timestamp = Instant.now().toEpochMilli();
        this.operation = operation;
        this.eventType = eventType;
        this.entityId = entityId;
        this.userId = userId;
    }
}
