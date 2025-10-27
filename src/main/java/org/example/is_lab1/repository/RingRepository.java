package org.example.is_lab1.repository;

import org.example.is_lab1.models.dto.RingDTO;
import org.example.is_lab1.models.entity.Ring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RingRepository extends JpaRepository<Ring, Integer> {
    @Query("""
        SELECT r FROM Ring r
        WHERE r.id NOT IN (
            SELECT b.ring.id FROM BookCreature b WHERE b.ring IS NOT NULL
        )
        """)
    List<Ring> findRingsWithoutOwner();
}
