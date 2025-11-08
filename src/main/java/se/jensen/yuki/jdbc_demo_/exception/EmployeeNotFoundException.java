package se.jensen.yuki.jdbc_demo_.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(int id) {
        super("No employee is found with ID=" + id);
    }
}
