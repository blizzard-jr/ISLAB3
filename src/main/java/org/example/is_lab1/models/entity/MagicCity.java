package org.example.is_lab1.models.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "entity"
)
public class MagicCity {
    @Id
    @GeneratedValue
    private int id;

    // Поле не может быть null, строка не может быть пустой
    @Check(constraints = "name IS NOT NULL AND TRIM(name) <> ''")
    private String name;

    // Значение поля должно быть больше 0, поле не может быть null
    @Check(constraints = "area > 0")
    @Column(nullable = false)
    private Long area;

    // Значение поля должно быть больше 0
    @Check(constraints = "population > 0")
    private int population;

    @Column(
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,
            updatable = false,
            nullable = false
    )
    @Generated(GenerationTime.INSERT)
    private java.time.LocalDateTime establishmentDate;

    // Поле не может быть null
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookCreatureType governor;

    // Поле не может быть null — логическое значение
    @Column(nullable = false)
    private Boolean capital;

    // Значение поля должно быть больше 0
    @Check(constraints = "population_density > 0")
    private Integer populationDensity;


    public MagicCity(String name, Long area, int population, String governor, Boolean capital, Integer populationDensity){
        this.area = area;
        this.capital = capital;
        this.governor = BookCreatureType.getType(governor);
        this.population = population;
        this.populationDensity = populationDensity;
        this.name = name;
    }

    public MagicCity(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getArea() {
        return area;
    }

    public int getPopulation() {
        return population;
    }

    public LocalDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public BookCreatureType getGovernor() {
        return governor;
    }

    public Boolean getCapital() {
        return capital;
    }

    public Integer getPopulationDensity() {
        return populationDensity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setEstablishmentDate(LocalDateTime establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public void setGovernor(BookCreatureType governor) {
        this.governor = governor;
    }

    public void setCapital(Boolean capital) {
        this.capital = capital;
    }

    public void setPopulationDensity(Integer populationDensity) {
        this.populationDensity = populationDensity;
    }

    @Override
    public String toString() {
        return "MagicCity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", area=" + area +
                ", population=" + population +
                ", establishmentDate=" + establishmentDate +
                ", governor=" + governor +
                ", capital=" + capital +
                ", populationDensity=" + populationDensity +
                '}';
    }
}

