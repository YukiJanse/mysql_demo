package se.jensen.yuki.jdbc_demo.service;

import se.jensen.yuki.jdbc_demo.dao.TeacherDao;
import se.jensen.yuki.jdbc_demo.model.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherService {
    private TeacherDao teacherDao;

    public TeacherService(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public List<Teacher> getAll() {
        List<Teacher> teacherList = new ArrayList<>();
        try {
            teacherList = teacherDao.findAll();
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
        return teacherList;
    }

    public Teacher getById(int id) {
        Teacher teacher = null;
        try {
            teacher = teacherDao.findById(id);
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
        return teacher;
    }

    public void addTeacher(Teacher teacher) {
        if (teacher.getFirstName().isEmpty() || teacher.getLastName().isEmpty()) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("First name and last name are required for registration");
        }
        try {
            teacherDao.insert(teacher);
        } catch (RuntimeException e) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException(e);
        }
    }

    public void deleteTeacher(int id) {
        if (id <= 0) {
            // IT MUST BE REPLACED TO ITS OWN EXCEPTION
            throw new RuntimeException("ID must be a positive number.");
        }
        teacherDao.delete(id);
    }
}
