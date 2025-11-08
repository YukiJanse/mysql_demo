package se.jensen.yuki.jdbc_demo_.exception;

public class EmployeeServiceException extends RuntimeException {
    public EmployeeServiceException(String message) {
        super(message);
    }
}
