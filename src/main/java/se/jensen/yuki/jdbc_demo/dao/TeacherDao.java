package se.jensen.yuki.jdbc_demo.dao;

import se.jensen.yuki.jdbc_demo.model.Teacher;

import java.util.List;

public interface TeacherDao extends GenericDao<Teacher> {
    List<Teacher> findAll();

    Teacher findById(int id);

    void insert(Teacher teacher);

    void delete(int id);
}
