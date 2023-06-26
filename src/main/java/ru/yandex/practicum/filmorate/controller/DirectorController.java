package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    @NonNull
    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirector(@PathVariable("id") int id) {
        return directorService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable("id") int id) {
        directorService.deleteDirector(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }
}