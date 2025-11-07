package se.jensen.yuki.jdbc_demo.service;

import se.jensen.yuki.jdbc_demo.model.Student;
import se.jensen.yuki.jdbc_demo.model.Teacher;

import java.util.ArrayList;
import java.util.List;

public class SchoolService {
    StudentService studentService;
    TeacherService teacherService;

    public SchoolService(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        try {
            studentList = studentService.getAll();
        } catch (RuntimeException e) {
            System.out.println("Failed to get All student info: " + e.getMessage());
        }
        return studentList;
    }

    public Student getStudentById(int id) {
        Student student = null;
        try {
            student = studentService.getById(id);
        } catch (RuntimeException e) {
            System.out.println("Failed to get student info with ID=" + id + "\n" + e.getMessage());
        }
        return student;
    }

    public void addStudent(Student student) {
        try {
            studentService.addStudent(student);
        } catch (RuntimeException e) {
            System.out.println("Failed to add student: " + e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        studentService.deleteStudent(id);
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        try {
            teacherList = teacherService.getAll();
        } catch (Exception e) {
            System.out.println("Failed to get All teacher info.");
        }
        return teacherList;
    }

    public Teacher getTeacherById(int id) {
        Teacher teacher = null;
        try {
            teacher = teacherService.getById(id);
        } catch (RuntimeException e) {
            System.out.println("Failed to get teacher info with ID=" + id + "\n" + e.getMessage());
        }
        return teacher;
    }

    public void addTeacher(Teacher teacher) {
        try {
            teacherService.addTeacher(teacher);
        } catch (RuntimeException e) {
            System.out.println("Failed to add teacher: " + e.getMessage());
        }
    }

    public void deleteTeacher(int id) {
        teacherService.deleteTeacher(id);
    }


    public void modifyRegisteredTeacher(Student student) {
        Teacher registeredTeacher = teacherService.getById(student.getTeacher().getId());
        try {
            if (registeredTeacher == null) {
                teacherService.addTeacher(student.getTeacher());
            }
            studentService.modifyRegisteredTeacher(student);
        } catch (Exception e) {
            System.out.println("modifyRegisteredTeacher: " + e.getMessage());
        }
    }
}
