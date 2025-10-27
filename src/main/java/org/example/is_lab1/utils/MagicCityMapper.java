package org.example.is_lab1.utils;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MagicCityMapper {
    @Mapping(target = "governor", expression = "java(BookCreatureType.getType(dto.governor()))")
    MagicCity toEntity(MagicCityDTO dto);
    MagicCityDTO toDto(MagicCity entity);

    @Mapping(target = "id", ignore = true) // id не трогаем
    void updateEntityFromDto(MagicCityDTO dto, @MappingTarget MagicCity entity);
}
