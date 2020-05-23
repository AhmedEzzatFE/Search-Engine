package com.mightyjava.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.*;
import java.net.URL;

@Entity
@Table(name = "trends")
public class Trends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private double name;
    @Column
    private double Count;
    @Column
    private double location;

    @Override
    public String toString() {
        return "Trends{" +
                "id=" + id +
                ", name=" + name +
                ", Count=" + Count +
                ", location=" + location +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getName() {
        return name;
    }

    public void setName(double name) {
        this.name = name;
    }

    public double getCount() {
        return Count;
    }

    public void setCount(double count) {
        Count = count;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }
}
