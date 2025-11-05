package org.example.is_lab1.controllers;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.services.CityService;
import org.example.is_lab1.services.InteractService;
import org.example.is_lab1.utils.MagicCityMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
public class CityController {
    private final CityService service;

    public CityController(CityService service, MagicCityMapper mapper) {
        this.service = service;
    }

    @PostMapping("/interact")
    public ResponseEntity<String> create(@RequestBody MagicCityDTO creature){
        String result = service.create(creature);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<String> modify(@PathVariable int id, @RequestBody MagicCityDTO creature){
        String result = service.modify(id, creature);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id){
        String result = service.delete(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/view")
    public ResponseEntity<Page<MagicCityDTO>> get(@RequestParam int page){
        return ResponseEntity.ok(service.get(page));
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<MagicCityDTO> getById(@PathVariable int id){
        return ResponseEntity.ok(service.getById(id));
    }
}
