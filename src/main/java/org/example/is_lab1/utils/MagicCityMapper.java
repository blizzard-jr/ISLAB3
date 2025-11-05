package org.example.is_lab1.utils;

import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.CityRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MagicCityMapper {

    @Autowired
    protected CityRepository cityRepository;

    // Если передан только id - возвращаем ссылку, иначе создаем новый
    public MagicCity toEntity(MagicCityDTO dto) {
        if (dto == null) {
            return null;
        }

        // Если передан только id без остальных полей - используем ссылку
        if (dto.id() != 0 && (dto.name() == null || dto.name().isBlank())) {
            return cityRepository.getReferenceById(dto.id());
        }

        // Иначе создаем новый объект через маппинг
        return mapToEntity(dto);
    }

    @Mapping(target = "governor", expression = "java(BookCreatureType.getType(dto.governor()))")
    @Mapping(target = "id", ignore = true) // При создании нового id генерируется
    protected abstract MagicCity mapToEntity(MagicCityDTO dto);

    public abstract MagicCityDTO toDto(MagicCity entity);

    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(MagicCityDTO dto, @MappingTarget MagicCity entity);
}
