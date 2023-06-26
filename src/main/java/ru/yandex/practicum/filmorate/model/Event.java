package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Event {
    @PositiveOrZero(message = "event_id меньше нуля")
    @NotNull(message = "Отсутствует event_id")
    int eventId = 0;
    @NotNull(message = "Отсутствует event_timestamp")
    Long eventTimestamp;
    @PositiveOrZero(message = "user_id меньше нуля")
    @NotNull(message = "Отсутствует user_id")
    int userId;
    @NotBlank(message = "Пустой event_type")
    String eventType;
    @NotBlank(message = "Пустой operation")
    String operation;
    @PositiveOrZero(message = "entity_id меньше нуля")
    @NotNull(message = "Отсутствует entity_id")
    int entityId;

    //Конструктор для события с созданием времени
    public Event(String operation, String entity, int userId, int entityId) {
        this.eventTimestamp = Instant.now().toEpochMilli();
        this.operation = operation;
        this.eventType = entity;
        this.entityId = entityId;
        this.userId = userId;
    }
}
