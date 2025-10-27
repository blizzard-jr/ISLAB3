package org.example.is_lab1.controllers;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.services.SpecialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/special")
public class SpecialController {
    private final SpecialService service;


    public SpecialController(SpecialService specialService){
        this.service = specialService;
    }

    @GetMapping("/defenseAbove/{value}")
    public ResponseEntity<Integer> getUpperDefenseLevel(@PathVariable int value){
        return ResponseEntity.ok(service.upperDefenseLevel(value));
    }

    @GetMapping("/nameMatcher/{value}")
    public ResponseEntity<List<BookCreatureDTO>> getListByName(@PathVariable String value){
        return ResponseEntity.ok(service.nameMatch(value));
    }

    @GetMapping("/defenseBelow/{value}")
    public ResponseEntity<List<BookCreatureDTO>> getListByDefense(@PathVariable int value){
        return ResponseEntity.ok(service.belowDefense(value));
    }

    @GetMapping("/swap/{id1}/{id2}")
    public ResponseEntity<String> swap(@PathVariable int id1, @PathVariable int id2){
        service.swapRings(id1, id2);
        return ResponseEntity.ok("Обмен прошёл успешно");
    }

    @GetMapping("/move")
    public ResponseEntity<String> moveToMordor(){
        service.moveInMordor();
        return ResponseEntity.ok("Обмен прошёл успешно");
    }






}
