package se.jensen.yuki.jdbc_demo.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
    private List<Student> studentList = new ArrayList<>();

    public Teacher() {
    }

    public Teacher(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public Teacher(int id, String firstName, String lastName) {
        super(id, firstName, lastName);
    }

    public Teacher(int id, String firstName, String lastName, List<Student> studentList) {
        super(id, firstName, lastName);
        this.studentList = studentList;
    }

    public void addStudent(Student student) {
        if (student == null) {
            throw new RuntimeException("Failed addStudent(): Instance is null.");
        }
        studentList.add(student);
    }

    public List<Student> getStudentList() {
        return studentList;
    }
}
