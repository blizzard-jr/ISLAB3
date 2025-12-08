package org.example.is_lab1.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO для парсинга YAML файлов импорта.
 * Структура YAML файла должна быть:
 * <pre>
 * creatures:
 *   - name: "Frodo"
 *     coordinates:
 *       x: 10
 *       y: 20.5
 *     age: 50
 *     creatureType: "HOBBIT"
 *     ...
 * </pre>
 */
public record ImportRequestDTO(
    @JsonProperty("creatures")
    List<BookCreatureDTO> creatures
) {
    public ImportRequestDTO {
        if (creatures == null) {
            throw new IllegalArgumentException("Creatures list cannot be null");
        }
    }
}






