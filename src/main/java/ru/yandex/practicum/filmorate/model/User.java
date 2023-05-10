package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public boolean addFriend(User user) {
        return friends.add(user.getId());
    }

    public boolean removeFriend(User user) {
        return friends.remove(user.getId());
    }
}
