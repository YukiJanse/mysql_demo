package se.jensen.yuki.jdbc_demo.service;

import se.jensen.yuki.jdbc_demo.dao.StudentDao;
import se.jensen.yuki.jdbc_demo.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private StudentDao studentDao;

    public StudentService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public List<Student> getAll() {
        List<Student> studentList = new ArrayList<>();
        try {
            studentList = studentDao.findAll();
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
        return studentList;
    }

    public Student getById(int id) {
        Student student = null;
        try {
            student = studentDao.findById(id);
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
        return student;
    }

    public void addStudent(Student student) {
        if (student.getFirstName().isEmpty() || student.getLastName().isEmpty()) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("First name and last name are required for registration");
        }
        try {
            studentDao.insert(student);
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
    }

    public void deleteStudent(int id) {
        if (id <= 0) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("ID must be a positive number.");
        }
        studentDao.delete(id);
    }

    public void modifyRegisteredTeacher(Student student) {
        if (student.getId() == 0) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("The student is not registered.");
        }

        try {
            Student registerdStudent = studentDao.findById(student.getId());
            if (registerdStudent != null) {
                try {
                    studentDao.updateTeacherId(student);
                } catch (RuntimeException e) {
                    // IT MUST BE REPLACED TO ITS OWN EXCEPTION
                    throw new RuntimeException("Failed to register teacher");
                }
            }
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("Failed to search the student");
        }
    }
}
