package org.example.is_lab1.models.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.Date;

@Entity
public class BookCreature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;//Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @Column(unique = true, nullable = false)
    @Check(constraints = "name IS NOT NULL AND TRIM(name) <> ''")
    private String name; //Поле не может быть null, Строка не может быть пустой, Уникальное

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_fk_id", referencedColumnName = "id")
    private Coordinates coordinates; //Поле не может быть null

    @Column(
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,
            updatable = false,
            nullable = false
    )
    @Generated(GenerationTime.INSERT)
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Check(constraints = "age > 0")
    private int age; //Значение поля должно быть больше 0

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookCreatureType creatureType; //Поле не может быть null

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "city_fk_id", referencedColumnName = "id")
    private MagicCity creatureLocation; //Поле может быть null

    @Check(constraints = "attack_level > 0")
    private Float attackLevel; //Значение поля должно быть больше 0, Поле может быть null

    @Check(constraints = "defense_level > 0")
    @Column(nullable = false)
    private Float defenseLevel; //Значение поля должно быть больше 0, Поле не может быть null

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ring_fk_id", referencedColumnName = "id")
    private Ring ring; //Поле может быть null


    public BookCreature(){}

    public BookCreature(String name, Coordinates coordinates, int age, BookCreatureType creatureType,
                 MagicCity creatureLocation, Float attackLevel, Float defenseLevel, Ring ring){
        this.name = name;
        this.age = age;
        this.attackLevel = attackLevel;
        this.coordinates = coordinates;
        this.creatureLocation = creatureLocation;
        this.creatureType = creatureType;
        this.defenseLevel = defenseLevel;
        this.ring = ring;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getAge() {
        return age;
    }

    public BookCreatureType getCreatureType() {
        return creatureType;
    }

    public MagicCity getCreatureLocation() {
        return creatureLocation;
    }

    public Float getAttackLevel() {
        return attackLevel;
    }

    public Float getDefenseLevel() {
        return defenseLevel;
    }

    public Ring getRing() {
        return ring;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCreatureType(BookCreatureType creatureType) {
        this.creatureType = creatureType;
    }

    public void setCreatureLocation(MagicCity creatureLocation) {
        this.creatureLocation = creatureLocation;
    }

    public void setAttackLevel(Float attackLevel) {
        this.attackLevel = attackLevel;
    }

    public void setDefenseLevel(Float defenseLevel) {
        this.defenseLevel = defenseLevel;
    }

    public void setRing(Ring ring) {
        this.ring = ring;
    }

    @Override
    public String toString() {
        return "BookCreature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", age=" + age +
                ", creatureType=" + creatureType +
                ", creatureLocation=" + creatureLocation.toString() +
                ", attackLevel=" + attackLevel +
                ", defenseLevel=" + defenseLevel +
                ", ring=" + ring.toString() +
                '}';
    }
}
