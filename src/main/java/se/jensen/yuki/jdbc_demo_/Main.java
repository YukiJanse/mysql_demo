package se.jensen.yuki.jdbc_demo_;

import se.jensen.yuki.jdbc_demo_.dao.EmployeeDaoImpl;
import se.jensen.yuki.jdbc_demo_.model.Employee;
import se.jensen.yuki.jdbc_demo_.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            EmployeeService employeeService = new EmployeeService(new EmployeeDaoImpl());
            Employee employee = new Employee(2, "Emil Janse", LocalDate.now(), 50000);
            employeeService.addEmployee(employee);
            showAllEmployees(employeeService);
            employee.setSalary(35000);
            employeeService.modifySalary(employee);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showAllEmployees(EmployeeService employeeService) {
        List<Employee> employeeList = employeeService.getAllEmployee();
        System.out.println("employeeList.size() = " + employeeList.size());
        for (Employee employee : employeeList) {
            System.out.println("Employee name: " + employee.getName());
            System.out.println("Employee hire date: " + employee.getHireDate());
            System.out.println("Employee salary: " + employee.getSalary());
        }
    }
}
