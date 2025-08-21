package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    //GET /directors
    @GetMapping
    public Collection<Director> findAllDirectors(){
        return directorService.findAllDirectors();
    }

    //GET /directors/{id}
    @GetMapping("/{id}")
    public  Director findDirectorById(@PathVariable Long id){
        return directorService.findDirectorById(id);
    }

    //POST /directors
    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director){
        return directorService.createDirector(director);
    }


    //PUT /directors
    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director newDirector){
        return directorService.updateDirector(newDirector);
    }

    // DELETE /directors/{id}
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id){
        directorService.deleteDirector(id);
    }

}
