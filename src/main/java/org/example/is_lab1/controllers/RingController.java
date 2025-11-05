package org.example.is_lab1.controllers;


import lombok.Data;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.services.CityService;
import org.example.is_lab1.services.RingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ring")
public class RingController {
    private final RingService service;

    public RingController(RingService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody RingDTO ring){
        String result = service.create(ring);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<String> modify(@PathVariable int id, @RequestBody RingDTO ring){
        String result = service.modify(id, ring);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id){
        String result = service.delete(id);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/view")
    public ResponseEntity<Page<RingDTO>> get(@RequestParam int page){
        return ResponseEntity.ok(service.get(page));
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<RingDTO> getById(@PathVariable int id){
        return ResponseEntity.ok(service.getById(id));
    }
}
