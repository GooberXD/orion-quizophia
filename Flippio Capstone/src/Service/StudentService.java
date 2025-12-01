package Service;

import Model.*;
import FileManager.*;
import java.io.*;
import java.util.*;

public class StudentService {
    private StudentFileManager fileManager;
    private List<Student> students;

    public StudentService(String fileName) {
        this.fileManager = new StudentFileManager(fileName);
        try {
            this.students = fileManager.load();
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
            // If file fails, start with empty list
            this.students = new java.util.ArrayList<>();
        }
    }

    // Business Logic: Find a student by their ID
    public Student getStudentById(String id) {
        for (Student s : students) {
            if (s.getIdNumber().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Business Logic: Save a student's progress after a quiz
    public void updateStudentProgress(Student student) {
        // Find the student in the list and update them (in memory)
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getIdNumber().equals(student.getIdNumber())) {
                students.set(i, student);
                break;
            }
        }
        // Save changes to file (Persistence)
        saveChanges();
    }

    // Helper to trigger file save
    private void saveChanges() {
        try {
            fileManager.save(students);
        } catch (IOException e) {
            System.err.println("Failed to save student data: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        return students;
    }
}
