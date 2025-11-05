package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.Ring;
import org.example.is_lab1.repository.RingRepository;
import org.example.is_lab1.utils.RingMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RingService {
    private final RingRepository repository;
    private final RingMapper mapper;
    private final WebSocketNotificationService notificationService;


    public String create(RingDTO ring){
//        Ring el = new Ring(ring.name(), ring.power(), ring.weight());
        Ring el = mapper.toEntity(ring);
        repository.save(el);
        notificationService.notifyCreated(el.getId(), mapper.toDto(el));
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
        notificationService.notifyUpdated(id, mapper.toDto(el));
        return "RingService modify";
    }

    public String delete(int id){
        repository.deleteById(id);
        notificationService.notifyDeleted(id);
        return "RingService delete";
    }

    public Page<RingDTO> get(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return repository.findRingsWithoutOwner(pageable).map(mapper::toDto);
    }

    public RingDTO getById(int id){
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Creature not found with id " + id)));
    }

}
