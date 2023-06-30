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
        log.info("Добавление режиссера: {}", director);
        return directorService.addDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Получение списка режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable("id") int id) {
        log.info("Получение данных о режиссере с id: {}", id);
        return directorService.getDirector(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") int id) {
        log.info("Удаление режиссера с id: {}", id);
        directorService.deleteDirector(id);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("Обновление данных о режиссере с id: {}", director.getId());
        return directorService.updateDirector(director);
    }
}