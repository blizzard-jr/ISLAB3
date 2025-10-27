package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.utils.MagicCityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository repository;
    private final MagicCityMapper mapper;


    public String create(MagicCityDTO city){
//        MagicCity el = new MagicCity(city.name(), city.area(), city.population(), city.governor(), city.capital(), city.populationDensity());
        MagicCity el = mapper.toEntity(city);
        repository.save(el);
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
        return "CityService modify";
    }

    public String delete(int id){
        repository.deleteById(id);
        return "CityService delete";
    }

    public List<MagicCityDTO> getAll(){
        return repository.findAll().stream().map(x -> mapper.toDto(x)).toList();
    }

    public MagicCityDTO getById(int id){
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Creature not found with id " + id)));
    }
}
