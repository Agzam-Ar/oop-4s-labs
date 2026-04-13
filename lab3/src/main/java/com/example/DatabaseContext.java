package com.example;

import com.example.models.Shape;
import com.example.models.ShapesGroup;
import com.example.repositories.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class DatabaseContext {

    private final AnnotationConfigApplicationContext ctx;
    private final ShapeRepository shapeRepo;
    private final ShapesGroupRepository shapesGroupRepo;

    public DatabaseContext() {
        ctx = new AnnotationConfigApplicationContext(JpaConfig.class);
        shapeRepo = ctx.getBean(ShapeRepository.class);
        shapesGroupRepo = ctx.getBean(ShapesGroupRepository.class);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getItems(Class<T> type) {
        if (type == Shape.class || type.getSuperclass() == Shape.class) {
            return (List<T>) shapeRepo.findAll();
        }
        if (type == ShapesGroup.class) {
            return (List<T>) shapesGroupRepo.findAll();
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> type, Integer id) {
        if (type == Shape.class || type.getSuperclass() == Shape.class) {
            return (T) shapeRepo.findById(id).orElse(null);
        }
        if (type == ShapesGroup.class) {
            return (T) shapesGroupRepo.findById(id).orElse(null);
        }
        return null;
    }

    public void add(Object entity) {
        if (entity instanceof Shape) {
            shapeRepo.save((Shape) entity);
        } else if (entity instanceof ShapesGroup) {
            shapesGroupRepo.save((ShapesGroup) entity);
        }
    }

    public void update(Object entity) {
        add(entity);
    }

    public void delete(Object entity) {
        if (entity instanceof Shape) {
            shapeRepo.delete((Shape) entity);
        } else if (entity instanceof ShapesGroup) {
            shapesGroupRepo.delete((ShapesGroup) entity);
        }
    }

    public void close() {
        ctx.close();
    }

}