package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable("id") int id) {
        return directorService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") int id) {
        directorService.deleteDirector(id);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }
}