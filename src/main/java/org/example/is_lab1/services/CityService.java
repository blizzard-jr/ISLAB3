package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.utils.MagicCityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository repository;
    private final MagicCityMapper mapper;
    private final WebSocketNotificationService notificationService;


    public String create(MagicCityDTO city){
//        MagicCity el = new MagicCity(city.name(), city.area(), city.population(), city.governor(), city.capital(), city.populationDensity());
        MagicCity el = mapper.toEntity(city);
        repository.save(el);
        notificationService.notifyCreated(el.getId(), mapper.toDto(el));
        return "CityService create";
    }

    @Transactional
    public String modify(int id, MagicCityDTO city){
        MagicCity el = repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        mapper.updateEntityFromDto(city, el);
//        el.setArea(city.area());
//        el.setCapital(city.capital());
//        el.setGovernor(BookCreatureType.getType(city.governor()));
//        el.setName(city.name());
//        el.setPopulation(city.population());
//        el.setPopulationDensity(city.populationDensity());
        repository.save(el);
        notificationService.notifyUpdated(id, mapper.toDto(el));
        return "CityService modify";
    }

    public String delete(int id){
        repository.deleteById(id);
        notificationService.notifyDeleted(id);
        return "CityService delete";
    }

    public Page<MagicCityDTO> get(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return repository.findAll(pageable).map(mapper::toDto);
    }

    public MagicCityDTO getById(int id){
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Creature not found with id " + id)));
    }
}
