package org.example.is_lab1.utils;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.CoordinatesDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.Coordinates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {MagicCityMapper.class, RingMapper.class}, imports = {BookCreatureType.class})
public interface BookCreatureMapper {

    @Mapping(target = "creatureType", expression = "java(BookCreatureType.getType(dto.creatureType()))")
    @Mapping(target = "creatureLocation", qualifiedByName = "toEntity")
    @Mapping(target = "ring", qualifiedByName = "toEntity")
    @Mapping(target = "coordinates", source = "coordinates", qualifiedByName = "coordinatesToEntity")
    @Mapping(target = "creationDate", ignore = true) // Генерируется автоматически
    BookCreature toEntity(BookCreatureDTO dto);

    @Mapping(target = "creatureType", expression = "java(entity.getCreatureType().name())")
    @Mapping(target = "coordinates", source = "coordinates", qualifiedByName = "coordinatesToDto")
    BookCreatureDTO toDto(BookCreature entity);

    // Обновление существующей сущности (при modify)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creatureType", expression = "java(BookCreatureType.getType(dto.creatureType()))")
    @Mapping(target = "creatureLocation", qualifiedByName = "toEntity")
    @Mapping(target = "ring", qualifiedByName = "toEntity")
    @Mapping(target = "coordinates", source = "coordinates", qualifiedByName = "coordinatesToEntity")
    void updateEntityFromDto(BookCreatureDTO dto, @MappingTarget BookCreature entity);

    @Named("coordinatesToEntity")
    default Coordinates coordinatesToEntity(CoordinatesDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Coordinates(dto.x(), dto.y());
    }

    @Named("coordinatesToDto")
    default CoordinatesDTO coordinatesToDto(Coordinates entity) {
        if (entity == null) {
            return null;
        }
        return new CoordinatesDTO(entity.getX(), entity.getY());
    }
}
