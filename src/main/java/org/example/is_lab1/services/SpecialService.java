package org.example.is_lab1.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.utils.BookCreatureMapper;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialService {
    private final EntityManager entityManager;
    private final BookCreatureMapper mapper;

    public SpecialService(EntityManager entityManager, BookCreatureMapper mapper){
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Transactional
    public List<BookCreatureDTO> nameMatch(String value) {
        Query query = entityManager.createNativeQuery("SELECT * FROM get_creatures_by_name_prefix(?1)", BookCreature.class);
        query.setParameter(1, value);
        List<BookCreatureDTO> ans = query.getResultList().stream().map(x -> mapper.toDto((BookCreature) x)).toList();
        return ans;
    }

    @Transactional
    public Integer upperDefenseLevel(int value){
        Query query = entityManager.createNativeQuery("SELECT count_creatures_with_defense_above(?1)");
        query.setParameter(1, value);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Transactional
    public List<BookCreatureDTO> belowDefense(int value) {
        Query query = entityManager.createNativeQuery("SELECT * FROM get_creatures_with_defense_below(?1)", BookCreature.class);
        query.setParameter(1, value);
        List<BookCreatureDTO> ans = query.getResultList().stream().map(x -> mapper.toDto((BookCreature) x)).toList();
        return ans;
    }


    @Transactional
    public void moveInMordor() {
        entityManager.createNativeQuery("SELECT move_hobbits_with_rings_to_mordor()").getSingleResult();
    }


    @Transactional
    public void swapRings(int id1, int id2) {
        Query query = entityManager.createNativeQuery("SELECT swap_rings(?1, ?2)");
        query.setParameter(1, id1);
        query.setParameter(2, id2);
        query.getSingleResult();
    }




}
