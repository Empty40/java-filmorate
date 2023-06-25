package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.List;

public interface EventDao {
    void addEvent(Event event);

    Collection<Event> getEventUser(int userId);
}
