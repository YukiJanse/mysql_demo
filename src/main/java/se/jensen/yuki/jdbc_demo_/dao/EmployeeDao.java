package se.jensen.yuki.jdbc_demo_.dao;

import se.jensen.yuki.jdbc_demo_.model.Employee;

import java.util.List;

public interface EmployeeDao {
    List<Employee> findAll();

    Employee findById(int id);

    void insert(Employee employee);

    void update(Employee employee);

    void delete(int id);
}
