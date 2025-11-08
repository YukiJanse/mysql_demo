package se.jensen.yuki.jdbc_demo_.dao;

import se.jensen.yuki.jdbc_demo_.model.Employee;
import se.jensen.yuki.jdbc_demo_.util.ConfigurationManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee";
        List<Employee> employeeList = new ArrayList<>();
        try (Connection con = ConfigurationManager.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int employee_id = rs.getInt("employee_id");
                String name = rs.getString("name");
                java.sql.Date sql_date = rs.getDate("hire_date");
                LocalDate hire_Date = sql_date.toLocalDate();
                double salary = rs.getDouble("salary");
                employeeList.add(new Employee(employee_id, name, hire_Date, salary));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find data from database: " + e.getMessage());
        }
        return employeeList;
    }

    @Override
    public Employee findById(int id) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";
        Employee employee = null;
        try (Connection con = ConfigurationManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            int employee_id = rs.getInt("employee_id");
            String name = rs.getString("name");
            java.sql.Date sql_date = rs.getDate("hire_date");
            LocalDate hire_Date = sql_date.toLocalDate();
            double salary = rs.getDouble("salary");
            employee = new Employee(employee_id, name, hire_Date, salary);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find the employee(id= " + id + ") " + e.getMessage());
        }
        return employee;
    }

    @Override
    public void insert(Employee employee) {
        String sql = "INSERT IGNORE INTO employee (employee_id, name, hire_date, salary) VALUES (?, ?, ? ,?)";
        try (Connection con = ConfigurationManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employee.getEmployeeId());
            ps.setString(2, employee.getName());
            ps.setDate(3, Date.valueOf(employee.getHireDate()));
            ps.setDouble(4, employee.getSalary());
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted == 0) {
                System.out.println("Nothing updated");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert data: " + e.getMessage());
        }
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employee SET salary = ? WHERE name = ?";
        try (Connection con = ConfigurationManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, employee.getSalary());
            ps.setString(2, employee.getName());
            int rowsUpdated = ps.executeUpdate();
            System.out.println("The amount of updated: " + rowsUpdated);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update the database: " + e.getMessage());
        }

    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        try (Connection con = ConfigurationManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No data deleted.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete data from database: " + e.getMessage());
        }
    }
}
