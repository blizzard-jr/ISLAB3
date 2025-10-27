package org.example.is_lab1.models.dto;

import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.Coordinates;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;

public record BookCreatureDTO(int id, String name, CoordinatesDTO coordinates, java.util.Date creationDate,  int age, String creatureType,
                              MagicCityDTO creatureLocation, Float attackLevel, Float defenseLevel, RingDTO ring) {}









