package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Event {
    @PositiveOrZero(message = "event_id меньше нуля")
    @NotNull(message = "Отсутствует event_id")
    int eventId;
    @NotNull(message = "Отсутствует event_timestamp")
    Timestamp eventTimestamp;
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
}
