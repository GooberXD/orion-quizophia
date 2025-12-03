package Service;

import Model.*;
import FileManager.*;
import Exception.*;
import java.util.*;

public class TeacherService {
    private TeacherFileManager fileManager;
    private List<Teacher> teachers;

    public TeacherService(String fileName) {
        this.fileManager = new TeacherFileManager(fileName);
        try {
            this.teachers = fileManager.load();
        } catch (FileReadException e) {
            System.err.println("Error loading teachers: " + e.getMessage());
            this.teachers = new java.util.ArrayList<>();
        }
    }

    public Teacher getTeacherById(String id) {
        for (Teacher t : teachers) {
            if (t.getIdNumber().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // Business Logic: A teacher creates a new quiz
    public void createQuiz(Teacher teacher, Quiz newQuiz) {
        // 1. Add quiz ID to teacher's record
        teacher.addCreatedQuiz(newQuiz.getQuizName()); // Using Name as ID for simplicity

        // 2. Update Teacher file
        updateTeacherRecord(teacher);

        // 3. Save the Quiz itself (using QuizFileManager)
        QuizFileManager quizMgr = new QuizFileManager("quizzes.txt");
        try {
            // Append the new quiz to the quizzes file
            quizMgr.save(newQuiz);
        } catch (java.io.IOException e) { // QuizFileManager.save still throws IOException
            System.err.println("Error saving new quiz: " + e.getMessage());
        }
    }

    private void updateTeacherRecord(Teacher updatedTeacher) {
        for (int i = 0; i < teachers.size(); i++) {
            if (teachers.get(i).getIdNumber().equals(updatedTeacher.getIdNumber())) {
                teachers.set(i, updatedTeacher);
                break;
            }
        }
        try {
            fileManager.save(teachers);
        } catch (FileWriteException e) {
            System.err.println("Failed to save teacher data.");
        }
    }
}
