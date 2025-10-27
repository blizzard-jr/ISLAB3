package org.example.is_lab1.utils;

import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RingMapper {

    Ring toEntity(RingDTO dto);
    RingDTO toDto(Ring entity);

    @Mapping(target = "id", ignore = true) // id не трогаем
    void updateEntityFromDto(RingDTO dto, @MappingTarget Ring entity);





}
