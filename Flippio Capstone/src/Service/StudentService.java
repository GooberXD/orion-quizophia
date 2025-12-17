package Service;

import Model.*;
import FileManager.*;
import Exception.*;
import java.util.*;

public class StudentService {
    private StudentFileManager fileManager;
    private List<Student> students;

    public StudentService(String fileName) {
        this.fileManager = new StudentFileManager(fileName);
        try {
            this.students = fileManager.load();
        } catch (FileReadException e) {
            System.err.println("Error loading students: " + e.getMessage());
            // If a file fails, start with an empty list
            this.students = new java.util.ArrayList<>();
        }
    }

    // Find a student by their ID
    public Student getStudentById(String id) {
        for (Student s : students) {
            if (s.getIdNumber().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Save a student's progress after a quiz
    public void updateStudentProgress(Student student) {
        // Find the student in the list and update them (in memory)
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getIdNumber().equals(student.getIdNumber())) {
                students.set(i, student);
                break;
            }
        }
        // Save changes to a file
        saveChanges();
    }

    // Helper to trigger file save
    private void saveChanges() {
        try {
            fileManager.save(students);
        } catch (FileWriteException e) {
            System.err.println("Failed to save student data: " + e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        return students;
    }
}
