package Service;

import Model.*;
import FileManager.*;
import Exception.*;
import java.io.*;
import java.util.*;

public class AuthService {
    private static AuthService instance;
    private List<Student> students;
    private List<Teacher> teachers; // Added list for Teachers

    private AuthService() {
        this.students = new ArrayList<>();
        this.teachers = new ArrayList<>();
        loadUsers();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private void loadUsers() {
        try {
            // Load Students
            StudentFileManager studentMgr = new StudentFileManager("students.csv");
            this.students = studentMgr.load();

            // Load Teachers
            TeacherFileManager teacherMgr = new TeacherFileManager("teachers.csv");
            this.teachers = teacherMgr.load();

        } catch (FileReadException e) {
            System.err.println("Warning: Could not load some user files. " + e.getMessage());
        }
    }

    // UPDATED LOGIN METHOD
    public User login(String id, String password) throws Exception {
        // 1. Check Students
        Optional<Student> student = students.stream()
                .filter(s -> s.getIdNumber().equals(id) && s.authenticate(password))
                .findFirst();

        if (student.isPresent()) return student.get();

        // 2. Check Teachers
        Optional<Teacher> teacher = teachers.stream()
                .filter(t -> t.getIdNumber().equals(id) && t.authenticate(password))
                .findFirst();

        if (teacher.isPresent()) return teacher.get();

        // 3. Failed
        throw new UserInputErrorException("Account with ID " + id + " does not exist or password is incorrect.");
    }

    // Method to create dummy teacher data for testing
    public void createDummyTeacher() {
        TeacherFileManager mgr = new TeacherFileManager("teachers.csv");
        List<Teacher> dummyTeachers = new ArrayList<>();
        dummyTeachers.add(new Teacher("9999", "admin", "Teacher Jay Vince"));
        try {
            mgr.save(dummyTeachers);
        } catch(FileWriteException e) {
//            e.printStackTrace();
        }
    }
}
