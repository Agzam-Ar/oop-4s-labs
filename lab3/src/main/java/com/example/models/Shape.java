package com.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Shape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter Integer id;

    private @Getter long timestamp = Instant.now().getEpochSecond();

    @Getter @Setter int x;
    @Getter @Setter int y;

    protected Shape() {}

    protected Shape(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract int left();
    public abstract int right();
    public abstract int top();
    public abstract int bottom();

    public long lifetime() {
        return Instant.now().getEpochSecond() - timestamp;
    }

    public void refresh() {
        this.timestamp = Instant.now().getEpochSecond();
    }

}
