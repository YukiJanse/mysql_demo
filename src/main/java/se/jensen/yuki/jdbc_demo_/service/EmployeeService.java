package se.jensen.yuki.jdbc_demo_.service;

import se.jensen.yuki.jdbc_demo_.dao.EmployeeDao;
import se.jensen.yuki.jdbc_demo_.exception.EmployeeNotFoundException;
import se.jensen.yuki.jdbc_demo_.exception.EmployeeServiceException;
import se.jensen.yuki.jdbc_demo_.model.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployee() {
        return employeeDao.findAll();
    }

    public List<Employee> getEmployeesWithSalaryAbove(double limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Salary must be a positive number");
        }
        List<Employee> all = employeeDao.findAll();
        List<Employee> result = new ArrayList<>();
        for (Employee e : all) {
            if (e.getSalary() >= limit) {
                result.add(e);
            }
        }
        return result;
    }

    public Employee getById(int id) {
        if (id <= 0) {
            throw new EmployeeServiceException("ID must be a positive number.");
        }
        Employee employee = employeeDao.findById(id);
        if (employee == null) {
            throw new EmployeeServiceException("No employee found with id= " + id);
        }
        return employee;
    }

    public void addEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new EmployeeServiceException("Name can not be blank");
        } else if (employee.getHireDate() == null || employee.getHireDate().isAfter(LocalDate.now())) {
            throw new EmployeeServiceException("Hire date can not be blank or future date.");
        } else if (employee.getSalary() < 0) {
            throw new EmployeeServiceException("Salary can not be a negative number");
        }
        employeeDao.insert(employee);
    }

    public void modifySalary(Employee targetEmployee) {
        List<Employee> employeeList = employeeDao.findAll();
        int targetId = targetEmployee.getEmployeeId();
        if (employeeList.stream()
                .anyMatch(e -> e.getEmployeeId() == targetId)) {
            employeeDao.update(targetEmployee);
        } else {
            throw new EmployeeNotFoundException(targetId);
        }
        /*
        for (Employee employee : employeeList) {
            try {
                if (employee.getEmployeeId() == targetEmployee.getEmployeeId()) {
                    employeeDao.update(targetEmployee);
                }
            } catch (RuntimeException e) {
                // It will be replaced an original exception.
                throw new RuntimeException(e);
            }
        }*/
    }
}
