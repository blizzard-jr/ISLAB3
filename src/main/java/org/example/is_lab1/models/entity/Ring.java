package org.example.is_lab1.models.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "entity"
)
public class Ring {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    @Check(constraints = "name IS NOT NULL AND TRIM(name) <> ''")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Check(constraints = "power > 0")
    private long power; //Значение поля должно быть больше 0

    @Check(constraints = "weight > 0")
    private double weight; //Значение поля должно быть больше 0

    public Ring(String name, long power, double weight){
        this.name = name;
        this.power = power;
        this.weight = weight;
    }

    public Ring(){}

    public Ring(int id, String name, long power, double weight){
        this.id = id;
        this.name = name;
        this.power = power;
        this.weight = weight;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPower() {
        return power;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Ring{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", power=" + power +
                ", weight=" + weight +
                '}';
    }
}
