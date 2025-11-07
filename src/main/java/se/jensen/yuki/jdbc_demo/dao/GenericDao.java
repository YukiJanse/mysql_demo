package se.jensen.yuki.jdbc_demo.dao;

import java.util.List;

public interface GenericDao<T> {
    List<T> findAll();

    T findById(int id);

    void insert(T t);

    void delete(int id);
}
