package com.example.models;

import com.example.annotations.Named;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Rect extends Shape {

    private @Getter @Setter int width;
    private @Getter @Setter int height;

    Rect() {}

    public Rect(@Named("width") int width, @Named("height") int height) {
        super(0, 0);
        this.width = width;
        this.height = height;
    }

    public Rect(@Named("x") int x, @Named("y") int y, @Named("width") int width, @Named("height") int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public int left() {
        return x;
    }

    @Override
    public int right() {
        return x + width;
    }

    @Override
    public int top() {
        return y;
    }

    @Override
    public int bottom() {
        return y + height;
    }

    public int hypot2() {
        return width * width + height * height;
    }

    public int maxSide() {
        return Math.max(width, height);
    }

}
