package se.jensen.yuki.jdbc_demo.dao;

import se.jensen.yuki.jdbc_demo.model.Student;

import java.util.List;

public interface StudentDao extends GenericDao<Student> {
    List<Student> findAll();

    Student findById(int id);

    void insert(Student student);

    void delete(int id);

    void updateTeacherId(Student student);
}
