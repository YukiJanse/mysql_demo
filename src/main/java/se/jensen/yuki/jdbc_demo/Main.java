package se.jensen.yuki.jdbc_demo;

import se.jensen.yuki.jdbc_demo.dao.StudentDaoImpl;
import se.jensen.yuki.jdbc_demo.dao.TeacherDaoImpl;
import se.jensen.yuki.jdbc_demo.model.Student;
import se.jensen.yuki.jdbc_demo.model.Teacher;
import se.jensen.yuki.jdbc_demo.service.SchoolService;
import se.jensen.yuki.jdbc_demo.service.StudentService;
import se.jensen.yuki.jdbc_demo.service.TeacherService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            TeacherService teacherService = new TeacherService(new TeacherDaoImpl());
            StudentService studentService = new StudentService(new StudentDaoImpl());
            SchoolService schoolService = new SchoolService(studentService, teacherService);
            schoolService.addStudent(new Student("Sixten", "Ulriksson"));

//            teacherService.addTeacher(new Teacher("Yuki", "Janse"));
//            Student student = studentService.getById(2);
//            student.setTeacher(teacherService.getById(1));
//            studentService.modifyRegisteredTeacher(student);
//            List<Teacher> teacherList = teacherService.getAll();
//            teacherList.forEach(Main::showTeacherInfo);
//            StudentDao studentDao = new StudentDaoImpl();
//            Student student = new Student(0, "Sixten", "Ulriksson", new Teacher(0, "Alva", "Cordeus"));
//            studentDao.insert(student);
//            List<Student> students = studentDao.findAll();
//            students.forEach(Main::showStudentInfo);
//            showStudentInfo(studentDao.findById(5));
            //studentDao.delete(6);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showStudentInfo(Student s) {
        System.out.println("Student ID: " + s.getId());
        System.out.println("First name: " + s.getFirstName());
        System.out.println("Last name: " + s.getLastName());
        if (s.getTeacher() != null) {
            System.out.println("Teacher ID: " + s.getTeacher().getId());
            System.out.println("First name: " + s.getTeacher().getFirstName());
            System.out.println("Last name: " + s.getTeacher().getLastName());
        } else {
            System.out.println("Teacher: No teacher registered");
        }
        System.out.println();
    }

    private static void showStudentInfo(List<Student> studentList) {
        for (Student s : studentList) {
            System.out.println("Student ID: " + s.getId());
            System.out.println("First name: " + s.getFirstName());
            System.out.println("Last name: " + s.getLastName());
        }
    }

    private static void showTeacherInfo(Teacher t) {
        System.out.println("Teacher ID: " + t.getId());
        System.out.println("First name: " + t.getFirstName());
        System.out.println("Last name: " + t.getLastName());
        List<Student> studentList = t.getStudentList();
        if (studentList.size() != 0) {
            showStudentInfo(studentList);
        } else {
            System.out.println("Student: No student registered");
        }
        System.out.println();
    }
}
