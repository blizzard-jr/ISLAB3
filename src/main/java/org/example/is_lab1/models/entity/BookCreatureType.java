package org.example.is_lab1.models.entity;

public enum BookCreatureType {
    HOBBIT,
    ELF,
    HUMAN,
    GOLLUM;

    public static BookCreatureType getType(String name){
        System.out.println("Тип " + name);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        try {
            return BookCreatureType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown creature type: " + name);
        }
    }
}
