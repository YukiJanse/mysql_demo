package se.jensen.yuki.jdbc_demo.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import se.jensen.yuki.jdbc_demo.model.Teacher;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherDaoImplTest {
    private static HikariDataSource testDataSource;
    private TeacherDaoImpl teacherDao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        // Create H2 an in-memory database for testing
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        testDataSource = new HikariDataSource(config);

    }

    @BeforeEach
    void setup() throws SQLException {
        // Initiate test DataSource
        teacherDao = new TeacherDaoImpl(testDataSource);

        // Clean data before every test
        try (Connection con = testDataSource.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS student");
            stmt.execute("DROP TABLE IF EXISTS teacher");
            // Create tables
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

            stmt.execute("INSERT INTO teacher (first_name, last_name) VALUES ('Anna', 'Andersson' )");
            stmt.execute("INSERT INTO teacher (first_name, last_name) VALUES ('Erik', 'Eriksson' )");
            stmt.execute("INSERT INTO student (first_name, last_name, teacher_id) VALUES ('Kalle', 'Karlsson', '1')");
            stmt.execute("INSERT INTO student (first_name, last_name, teacher_id) VALUES ('Maria', 'Persson', '1')");

        }
    }

    @AfterAll
    static void tearDown() {
        if (testDataSource != null) {
            testDataSource.close();
        }
    }

    @Test
    @DisplayName("Find all teachers")
    void testFindAll() {

        // Act
        List<Teacher> teachers = teacherDao.findAll();

        // Assert
        assertEquals(2, teachers.size());
        assertEquals("Anna", teachers.get(0).getFirstName());
        assertEquals("Erik", teachers.get(1).getFirstName());
        assertEquals(2, teachers.get(0).getStudentList().size());
    }

    @Test
    @DisplayName("Return null when teacher does not exist")
    void testFindByIdNotFound() {
        // Act
        Teacher found = teacherDao.findById(999);

        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Find teacher by id")
    void testFindByIdFound() {
        // Act
        Teacher found = teacherDao.findById(1);

        // Assert
        assertEquals("Anna", found.getFirstName());
    }

    @Test
    @DisplayName("Create teacher")
    void testInsertTeacherWithoutStudentList() {
        Teacher teacher = new Teacher(0, "Kalle", "Karlsson", null);

        // Act
        teacherDao.insert(teacher);
        Teacher found = teacherDao.findById(3);

        // Assert
        assertNotNull(found);
        assertEquals("Kalle", found.getFirstName());
        assertEquals(0, found.getStudentList().size());
    }

    @Test
    @DisplayName("Delete teacher")
    void testDeleteStudent() {
        // Act
        teacherDao.delete(2);
        Teacher found = teacherDao.findById(2);

        // Assert
        assertNull(found);
    }
}