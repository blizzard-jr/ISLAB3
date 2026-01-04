package org.example.is_lab1.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.example.is_lab1.infra.HibernateStatisticsService;
import org.hibernate.stat.Statistics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(
        name = "cache.stats.enabled",
        havingValue = "true"
)
public class L2CacheStatsAspect {

    private final HibernateStatisticsService statsService;

    public L2CacheStatsAspect(HibernateStatisticsService statsService) {
        this.statsService = statsService;
    }

    @After("within(@org.springframework.stereotype.Repository *)")
    public void logStats() {
        Statistics stats = statsService.getStatistics();

        System.out.println("L2 Cache Hits: " + stats.getSecondLevelCacheHitCount());
        System.out.println("L2 Cache Misses: " + stats.getSecondLevelCacheMissCount());
        System.out.println("L2 Cache Puts: " + stats.getSecondLevelCachePutCount());
    }
}
