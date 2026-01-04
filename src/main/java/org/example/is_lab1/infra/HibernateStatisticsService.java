package org.example.is_lab1.infra;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Component
public class HibernateStatisticsService {

    @PersistenceUnit
    private EntityManagerFactory emf;

    public Statistics getStatistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }
}
