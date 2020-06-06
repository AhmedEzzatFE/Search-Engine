package com.SearchEngine.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.*;

@Entity
@Table(name = "trends")
public class Trends {

    @Column
    private String name;
    @Column
    private int Count;
    @Column
    private String location;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int primaryid;
    @Column
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Trends{" +
                "name='" + name + '\'' +
                ", Count=" + Count +
                ", location='" + location + '\'' +
                ", primary=" + primaryid +
                ", id=" + id +
                '}';
    }

    public int getPrimary() {
        return primaryid;
    }

    public void setPrimary(int primary) {
        this.primaryid = primary;
    }




}
