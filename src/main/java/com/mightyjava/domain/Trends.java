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
    private String name;
    @Column
    private int Count;
    @Column
    private String location;

    @Override
    public String toString() {
        return "Trends{" +
                "id=" + id +
                ", name=" + name +
                ", Count=" + Count +
                ", location=" + location +
                '}';
    }


}
