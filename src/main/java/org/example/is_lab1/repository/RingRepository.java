package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.Ring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RingRepository extends JpaRepository<Ring, Integer> {
    @Query("""
        SELECT r FROM Ring r
        WHERE r.id NOT IN (
            SELECT b.ring.id FROM BookCreature b WHERE b.ring IS NOT NULL
        )
        """)
    Page<Ring> findRingsWithoutOwner(Pageable pageable);
}