package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.MagicCityDTO;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.BookCreatureType;
import org.example.is_lab1.models.entity.MagicCity;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.repository.RingRepository;
import org.example.is_lab1.utils.RingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RingService {
    private final RingRepository repository;
    private final RingMapper mapper;


    public String create(RingDTO ring){
//        Ring el = new Ring(ring.name(), ring.power(), ring.weight());
        Ring el = mapper.toEntity(ring);
        repository.save(el);
        return "RingService create";
    }

    @Transactional
    public String modify(int id, RingDTO ring){
        Ring el = repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
//        el.setName(ring.name());
//        el.setPower(ring.power());
//        el.setWeight(ring.weight());
        mapper.updateEntityFromDto(ring, el);
        repository.save(el);
        return "RingService modify";
    }

    public String delete(int id){
        repository.deleteById(id);
        return "RingService delete";
    }

    public List<RingDTO> getAll(){
        return repository.findRingsWithoutOwner().stream().map(x -> mapper.toDto(x)).toList();
    }

    public RingDTO getById(int id){
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Creature not found with id " + id)));
    }

}
