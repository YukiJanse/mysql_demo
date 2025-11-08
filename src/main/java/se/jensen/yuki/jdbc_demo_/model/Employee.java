package se.jensen.yuki.jdbc_demo_.model;

import java.time.LocalDate;

public class Employee implements Person {
    private int employeeId;
    private String name;
    private LocalDate hireDate;
    private double salary;

    public Employee() {
        // For creating an object from database
    }

    public Employee(int employeeId, String name, LocalDate hireDate, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
