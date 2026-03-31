package com.example;

import com.example.models.Shape;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class DatabaseContext {

    private final SessionFactory sessionFactory;

    public DatabaseContext() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        sessionFactory = configuration.buildSessionFactory();
    }

    public <T extends Shape> List<T> getItems(Class<T> type) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + type.getSimpleName(), type).getResultList();
        }
    }

    public <T extends Shape> T findById(Class<T> type, Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(type, id);
        }
    }

    public void add(Object shape) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(shape);
            session.getTransaction().commit();
        }
    }

    public void update(Object shape) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(shape);
            session.getTransaction().commit();
        }
    }

    public void delete(Object shape) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(session.contains(shape) ? shape : session.merge(shape));
            session.getTransaction().commit();
        }
    }

}
