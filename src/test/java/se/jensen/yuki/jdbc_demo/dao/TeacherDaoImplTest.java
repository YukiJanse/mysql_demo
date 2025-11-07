package se.jensen.yuki.jdbc_demo.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import se.jensen.yuki.jdbc_demo.model.Student;
import se.jensen.yuki.jdbc_demo.model.Teacher;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherDaoImplTest {
    private static HikariDataSource testDataSource;
    private StudentDaoImpl studentDao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        // Create H2 in-memory database for testing
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        testDataSource = new HikariDataSource(config);

        // Create tables
        try (Connection con = testDataSource.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("""
                    CREATE TABLE teacher (
                        teacher_id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(50),
                        last_name VARCHAR(50)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE student (
                        student_id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(50),
                        last_name VARCHAR(50),
                        teacher_id INT,
                        FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id)
                    )
                    """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        // Initiate test DataSource
        studentDao = new StudentDaoImpl(testDataSource);

        // Clean data before every test
        try (Connection con = testDataSource.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DELETE FROM student");
            stmt.execute("DELETE FROM teacher");
            stmt.execute("ALTER TABLE student ALTER COLUMN student_id RESTART WITH 1");
            stmt.execute("ALTER TABLE teacher ALTER COLUMN teacher_id RESTART WITH 1");
        }
    }

    @AfterAll
    static void tearDown() {
        if (testDataSource != null) {
            testDataSource.close();
        }
    }

    @Test
    @DisplayName("Find all students")
    void testFindAll() {
        // Arrange - create testdata
        Student student1 = new Student(0, "Anna", "Andersson", null);
        Student student2 = new Student(0, "Erik", "Eriksson", null);
        studentDao.insert(student1);
        studentDao.insert(student2);

        // Act
        List<Student> students = studentDao.findAll();

        // Assert
        assertEquals(2, students.size());
        assertEquals("Anna", students.get(0).getFirstName());
        assertEquals("Erik", students.get(1).getFirstName());
    }

    @Test
    @DisplayName("Return null when student does not exist")
    void testFindByIdNotFound() {
        // Act
        Student found = studentDao.findById(999);

        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Create student without teacher")
    void testInsertStudentWithoutTeacher() {
        Student student = new Student(0, "Kalle", "Karlsson", null);

        // Act
        studentDao.insert(student);
        Student found = studentDao.findById(1);

        // Assert
        assertNotNull(found);
        assertEquals("Kalle", found.getFirstName());
        assertNull(found.getTeacher());
    }

    @Test
    @DisplayName("Create student with teacher")
    void testInsertStudentWithTeacher() {
        // Arrange
        Teacher teacher = new Teacher(0, "Gunnar", "Gustavsson");
        Student student = new Student(0, "Maria", "Persson", teacher);

        // Act
        studentDao.insert(student);
        Student found = studentDao.findById(1);

        // Assert
        assertNotNull(found);
        assertEquals("Maria", found.getFirstName());
        assertNotNull(found.getTeacher());
        assertEquals("Gunnar", found.getTeacher().getFirstName());
    }

    @Test
    @DisplayName("Delete student")
    void testDeleteStudent() {
        // Arrange
        Student student = new Student(0, "David", "Davidsson", null);
        studentDao.insert(student);

        // Act
        studentDao.delete(1);
        Student found = studentDao.findById(1);

        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Update teacher ID")
    void testUpdateTeacherId() {
        // Arrange
        Student student = new Student(0, "Peter", "Pettersson", null);
        studentDao.insert(student);

        Teacher teacher = new Teacher(1, "Carl", "Carlsson");
        student.setId(1);
        student.setTeacher(teacher);

        // Create the teacher in teacher table
        try (Connection con = testDataSource.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO teacher (first_name, last_name) VALUES ('Carl', 'Carlsson')");
        } catch (SQLException e) {
            fail("Failed to create teacher");
        }

        // Act
        studentDao.updateTeacherId(student);
        Student found = studentDao.findById(1);

        // Assert
        assertNotNull(found.getTeacher());
        assertEquals("Carl", found.getTeacher().getFirstName());
    }

    @Test
    @DisplayName("Throw exception when teacher is null by updating")
    void testUpdateTeacherIdWithNullTeacher() {
        // Arrange
        Student student = new Student(1, "Test", "Student", null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            studentDao.updateTeacherId(student);
        });
    }
}