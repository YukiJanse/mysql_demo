package se.jensen.yuki.jdbc_demo.model;

public class Student extends Person {
    private Teacher teacher;

    public Student() {
    }

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public Student(int id, String firstName, String lastName) {
        super(id, firstName, lastName);
    }

    public Student(int id, String firstName, String lastName, Teacher teacher) {
        super(id, firstName, lastName);
        this.teacher = teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
