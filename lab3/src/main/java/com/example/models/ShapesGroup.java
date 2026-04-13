package com.example.models;

import com.example.annotations.*;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class ShapesGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Get Integer id;

    protected @Get String name;

    @OneToMany(mappedBy = "groupEntity", fetch = FetchType.EAGER)
    private Set<Shape> shapes;

    protected ShapesGroup() {}

    public ShapesGroup(@Named("name") String name) {
        this.name = name;
    }
    
    public String shapes() {
        if (shapes == null || shapes.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Shape s : shapes) {
            if (!first) sb.append(", ");
            sb.append(s.getClass().getSimpleName()).append("@").append(s.id);
            first = false;
        }
        return sb.append("]").toString();
    }

}