package Service;

import Model.*;
import FileManager.*;
import Exception.*;
import java.io.*;
import java.util.*;

public class AuthService {
    private static AuthService instance;
    private List<Student> students;
    private List<Teacher> teachers;

    // File Paths
    private final String STUDENT_FILE = "students.csv";
    private final String TEACHER_FILE = "teachers.csv";

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
            StudentFileManager studentMgr = new StudentFileManager(STUDENT_FILE);
            this.students = studentMgr.load();
            TeacherFileManager teacherMgr = new TeacherFileManager(TEACHER_FILE);
            this.teachers = teacherMgr.load();

        } catch (FileReadException e) {
            System.err.println("Warning: Could not load some user files. " + e.getMessage());
        }
    }

    // --- EXISTING LOGIN METHOD ---
    public User login(String id, String password) throws Exception {
        // 1. Check Students
        Optional<Student> student = students.stream()
                .filter(s -> s.getIdNumber().equals(id) && s.authenticate(password))
                .findFirst();

        if (student.isPresent()) return student.get();
        Optional<Teacher> teacher = teachers.stream()
                .filter(t -> t.getIdNumber().equals(id) && t.authenticate(password))
                .findFirst();
        if (teacher.isPresent()) return teacher.get();

        throw new UserInputErrorException("Account with ID " + id + " does not exist or password is incorrect.");
    }

    // --- NEW SIGN UP METHOD ---
    public void register(String id, String password, String name, String role) throws Exception {
        boolean idExistsInStudents = students.stream().anyMatch(s -> s.getIdNumber().equals(id));
        boolean idExistsInTeachers = teachers.stream().anyMatch(t -> t.getIdNumber().equals(id));

        if (idExistsInStudents || idExistsInTeachers) {
            throw new UserInputErrorException("The ID Number '" + id + "' is already registered.");
        }
        if (role.equalsIgnoreCase("student")) {
            User newStudent = new Student(id, password, name);
            students.add((Student) newStudent);
            StudentFileManager mgr = new StudentFileManager(STUDENT_FILE);
            mgr.save(students);

        } else if (role.equalsIgnoreCase("teacher")) {
            User newTeacher = new Teacher(id, password, name);
            teachers.add((Teacher) newTeacher);
            TeacherFileManager mgr = new TeacherFileManager(TEACHER_FILE);
            mgr.save(teachers);

        } else {
            throw new UserInputErrorException("Invalid Role. Please enter 'Student' or 'Teacher'.");
        }
    }
}