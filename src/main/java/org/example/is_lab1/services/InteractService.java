package org.example.is_lab1.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.*;
import org.example.is_lab1.repository.CityRepository;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.repository.RingRepository;
import org.example.is_lab1.utils.BookCreatureMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InteractService {
    private final InteractRepository repository;
    private final BookCreatureMapper mapper;
    private final RingRepository ringRepository;
    private final CityRepository cityRepository;


    public InteractService(InteractRepository repository, BookCreatureMapper mapper, RingRepository ringRepository, CityRepository cityRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.ringRepository = ringRepository;
        this.cityRepository = cityRepository;
    }

    public String create(BookCreatureDTO creature){
//        BookCreatureType creatureType = BookCreatureType.valueOf(creature.creatureType());
//        Coordinates coordinates = new Coordinates(creature.coordinates().x(), creature.coordinates().y());
//        MagicCity creatureLocation = new MagicCity(creature.creatureLocation().name(), creature.creatureLocation().area(),
//                creature.creatureLocation().population(), creature.creatureLocation().governor(), creature.creatureLocation().
//                capital(), creature.creatureLocation().populationDensity());
//        Ring ring = new Ring(creature.ring().name(), creature.ring().power(), creature.ring().weight());
//        BookCreature el = new BookCreature(creature.name(), coordinates, creature.age(), creatureType, creatureLocation, creature.attackLevel(), creature.defenseLevel(), ring);
        BookCreature el = mapper.toEntity(creature);
        // ⚙️ Если город уже существует — берём ссылку, а не создаём новый
        if (creature.creatureLocation() != null && creature.creatureLocation().id() != 0) {
            el.setCreatureLocation(
                    cityRepository.getReferenceById(creature.creatureLocation().id())
            );
        }

        // ⚙️ Если кольцо уже существует — берём ссылку, а не создаём новый
        if (creature.ring() != null && creature.ring().id() != 0) {
            el.setRing(
                    ringRepository.getReferenceById(creature.ring().id())
            );
        }
        repository.save(el);
        return "Finish create";
    }

    @Transactional
    public String modify(int id, BookCreatureDTO creature){
        BookCreature existing =  repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        mapper.updateEntityFromDto(creature, existing);
//
//        existing.setName(creature.name());
//        existing.setAge(creature.age());
//        existing.setAttackLevel(creature.attackLevel());
//        existing.setCreatureType(BookCreatureType.getType(creature.creatureType()));
//        existing.setDefenseLevel(creature.defenseLevel());
//        Coordinates coordinates = new Coordinates(creature.coordinates().x(), creature.coordinates().y());
//        MagicCity city = new MagicCity(creature.creatureLocation().name(), creature.creatureLocation().area(), creature.creatureLocation().population(),
//                creature.creatureLocation().governor(), creature.creatureLocation().capital(), creature.creatureLocation().populationDensity());
//        existing.setCoordinates(coordinates);
//        existing.setCreatureLocation(city);
//        Ring ring = new Ring(creature.ring().name(), creature.ring().power(), creature.ring().weight());
//        existing.setRing(ring);

        repository.save(existing);
        return "Finish modify";
    }

    public String delete(int id){
        BookCreature creature = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Существо не найдено"));

        MagicCity city = creature.getCreatureLocation();

        // Удаляем существо
        repository.delete(creature);

        // Проверяем — остались ли ещё жители этого города
        if (city != null) {
            boolean hasResidents = repository.existsByCreatureLocation(city);
            if (!hasResidents) {
                cityRepository.delete(city);
            }
        }
        repository.deleteById(id);
        return "Finish delete";
    }

    public List<BookCreatureDTO> getAll(){
        return repository.findAll().stream().map(x -> mapper.toDto(x)).toList();
    }

    public BookCreatureDTO getById(int id){
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Creature not found with id " + id)));
    }



}
