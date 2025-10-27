package org.example.is_lab1.controllers;

import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.services.InteractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InteractController {
    private final InteractService service;

    public InteractController(InteractService service) {
        this.service = service;
    }

    @PostMapping("/interact")
    public ResponseEntity<String> create(@RequestBody BookCreatureDTO creature){
        String result = service.create(creature);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<String> modify(@PathVariable int id, @RequestBody BookCreatureDTO creature){
        String result = service.modify(id, creature);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id){
        String result = service.delete(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/view")
    public ResponseEntity<List<BookCreatureDTO>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<BookCreatureDTO> getById(@PathVariable int id){
        return ResponseEntity.ok(service.getById(id));
    }

}
