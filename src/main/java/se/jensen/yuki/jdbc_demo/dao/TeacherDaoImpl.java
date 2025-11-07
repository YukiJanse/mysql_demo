package se.jensen.yuki.jdbc_demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.jensen.yuki.jdbc_demo.model.Student;
import se.jensen.yuki.jdbc_demo.model.Teacher;
import se.jensen.yuki.jdbc_demo.util.ConfigurationManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherDaoImpl implements TeacherDao {
    private static final Logger logger = LoggerFactory.getLogger(TeacherDaoImpl.class);
    private final DataSource dataSource;

    public TeacherDaoImpl() {
        dataSource = ConfigurationManager.getInstance().getDataSource();
    }

    public TeacherDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Teacher> findAll() {
        String sql = """
                SELECT t.teacher_id, t.first_name AS t_first_name, t.last_name AS t_last_name, s.student_id, s.first_name AS s_first_name, s.last_name AS s_last_name
                FROM teacher t
                LEFT JOIN student s
                ON t.teacher_id = s.teacher_id
                """;
        // List<Teacher> teacherList = new ArrayList<>();
        // Map to avoid duplicated teacher objects
        Map<Integer, Teacher> teacherMap = new HashMap<>();
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int teacherId = rs.getInt("teacher_id");
                String tFirstName = rs.getString("t_first_name");
                String tLastName = rs.getString("t_last_name");
                Teacher teacher = teacherMap.computeIfAbsent(teacherId, id -> new Teacher(id, tFirstName, tLastName));
                int studentId = rs.getInt("student_id");
                if (!rs.wasNull()) {
                    String sFirstName = rs.getString("s_first_name");
                    String sLastName = rs.getString("s_last_name");
                    teacher.addStudent(new Student(studentId, sFirstName, sLastName, teacher));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read data: " + e.getMessage());
        }
//        return teacherList;
        return new ArrayList<>(teacherMap.values());
    }

    @Override
    public Teacher findById(int id) {
        Teacher teacher = null;
        String sql = """
                SELECT t.teacher_id, t.first_name AS t_first_name, t.last_name AS t_last_name, s.student_id, s.first_name AS s_first_name, s.last_name AS s_last_name
                FROM teacher t
                LEFT JOIN student s
                ON t.teacher_id = s.teacher_id
                WHERE t.teacher_id = ?
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int teacherId = rs.getInt("teacher_id");
                    String tFirstName = rs.getString("t_first_name");
                    String tLastName = rs.getString("t_last_name");
                    if (teacher == null) {
                        teacher = new Teacher(teacherId, tFirstName, tLastName);
                    }
                    int studentId = rs.getInt("student_id");
                    if (!rs.wasNull()) {
                        String sFirstName = rs.getString("s_first_name");
                        String sLastName = rs.getString("s_last_name");
                        teacher.addStudent(new Student(studentId, sFirstName, sLastName));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to read data: " + e.getMessage());
        }
        return teacher;
    }

    @Override
    public void insert(Teacher teacher) {
        String sql = """
                INSERT INTO teacher (first_name, last_name) VALUES (?, ?)
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            int insertedRow = ps.executeUpdate();
            if (insertedRow == 0) {
                System.out.println("No data inserted.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert data: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = """
                DELETE FROM teacher
                WHERE teacher_id = ?
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int deletedRows = ps.executeUpdate();
            if (deletedRows == 0) {
                System.out.println("No data deleted.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete data: " + e.getMessage());
        }
    }
}
