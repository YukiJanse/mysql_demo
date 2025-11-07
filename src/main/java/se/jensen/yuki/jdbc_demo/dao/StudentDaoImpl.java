package se.jensen.yuki.jdbc_demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.jensen.yuki.jdbc_demo.model.Student;
import se.jensen.yuki.jdbc_demo.model.Teacher;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;
import static se.jensen.yuki.jdbc_demo.util.ConfigurationManager.getInstance;


public class StudentDaoImpl implements StudentDao {
    private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);
    private final DataSource dataSource;

    public StudentDaoImpl() {
        dataSource = getInstance().getDataSource();
    }

    public StudentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Student> findAll() {
        logger.debug("Finding all students");
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.student_id, s.first_name, s.last_name, t.teacher_id, t.first_name as t_first_name, t.last_name as t_last_name" +
                " FROM student s LEFT JOIN teacher t ON s.teacher_id = t.teacher_id";
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapStudentObj(rs));
            }
            logger.info("Found {} students", students.size());
        } catch (SQLException e) {
            logger.error("Failed to read database", e);
            throw new RuntimeException("Failed to read database: " + e.getMessage());
        }

        return students;
    }

    @Override
    public Student findById(int id) {
        logger.debug("Finding student by id: {}", id);
        String sql = """
                SELECT s.student_id, s.first_name, s.last_name, t.teacher_id,
                t.first_name as t_first_name,
                t.last_name as t_last_name 
                FROM student s LEFT JOIN teacher t ON s.teacher_id = t.teacher_id 
                WHERE student_id = ?
                """;
        Student student = null;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = mapStudentObj(rs);
                    logger.info("Found student with id:{} Name: {} {}", id, student.getFirstName(), student.getLastName());
                } else {
                    logger.warn("No student found with id: {}", id);
                }
            }

        } catch (SQLException e) {
            logger.error("Failed to read database with id: {}", id, e);
            throw new RuntimeException("Failed to read database: " + e.getMessage());
        }
        return student;
    }

    @Override
    public void insert(Student student) {
        logger.debug("Inserting student: {} {}", student.getFirstName(), student.getLastName());
        try (Connection con = dataSource.getConnection()) {
            // Start transaction
            con.setAutoCommit(false);

            try {
                Teacher teacher = student.getTeacher();
                int teacher_id = 0;
                if (teacher != null) {
                    logger.debug("Getting or creating teacher: {} {}", teacher.getFirstName(), teacher.getLastName());
                    teacher_id = getOrCreateTeacherId(con, teacher);
                }

                String sql = "INSERT INTO student (first_name, last_name, teacher_id) VALUES (?, ? ,?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, student.getFirstName());
                    ps.setString(2, student.getLastName());
                    if (teacher != null) {
                        ps.setInt(3, teacher_id);
                    } else {
                        ps.setNull(3, NULL);
                    }
                    ps.executeUpdate();
                }
                // Commit transaction
                con.commit();
                logger.info("Successfully inserted student: {} {}", student.getFirstName(), student.getLastName());
            } catch (SQLException e) {
                logger.error("Failed to insert student, rolling back transaction", e);
                // Rollback transaction if something went wrong.
                con.rollback();
                throw new RuntimeException("Failed to insert data: " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.error("Failed to get database connection", e);
            throw new RuntimeException("Failed to insert data: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        logger.debug("Deleting student with id: {}", id);
        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int deletedRows = ps.executeUpdate();
            if (deletedRows > 0) {
                logger.info("Successfully deleted student with id: {}", id);
            } else {
                logger.warn("No student fond to delete with id: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Failed to delete student with id: {}", id, e);
            throw new RuntimeException("Failed to delete data: " + e.getMessage());
        }
    }

    @Override
    public void updateTeacherId(Student student) {
        logger.debug("Updating teacher for student id: {}", student.getId());
        if (student.getTeacher() == null) {
            logger.error("Attempted to update student with null teacher");
            throw new RuntimeException("The student has no teacher.");
        }
        String sql = """
                UPDATE student
                SET teacher_id = ?
                WHERE student_id = ?
                """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, student.getTeacher().getId());
            ps.setInt(2, student.getId());
            int updateRows = ps.executeUpdate();
            if (updateRows > 0) {
                logger.info("Successfully updated teacher for student id: {}", student.getId());
            } else {
                logger.warn("No student found to update with id: {}", student.getId());
                throw new RuntimeException("Nothing updated");
            }
        } catch (SQLException e) {
            logger.error("Failed to update teacher for student id: {}", student.getId(), e);
            throw new RuntimeException("Failed to update teacher:" + e.getMessage());
        }
    }

    private Student mapStudentObj(ResultSet rs) throws SQLException {
        int id = rs.getInt("student_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        int teacher_id = rs.getInt("teacher_id");
        Teacher teacher = null;
        if (!rs.wasNull()) {
            teacher =
                    new Teacher(rs.getInt("teacher_id"), rs.getString("t_first_name"), rs.getString("t_last_name"));
        }
        return new Student(id, firstName, lastName, teacher);
    }

    private int getOrCreateTeacherId(Connection con, Teacher teacher) throws SQLException {
        String selectSql = "SELECT teacher_id FROM teacher WHERE first_name = ? AND last_name = ?";
        try (PreparedStatement ps = con.prepareStatement(selectSql)) {
            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int teacherID = rs.getInt("teacher_id");
                    logger.debug("Found existing teacher with id: {}", teacherID);
                    return teacherID;
                }
            }
        }

        logger.debug("Creating new teacher: {} {}", teacher.getFirstName(), teacher.getLastName());
        String insertSql = "INSERT INTO teacher (first_name, last_name) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, teacher.getFirstName());
            ps.setString(2, teacher.getLastName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int teacherId = rs.getInt(1);
                    logger.info("Created new teacher with id: {}", teacherId);
                    return teacherId;
                }
            }
        }
        throw new SQLException("Failed to get teacher ID or create teacher");
    }
}
