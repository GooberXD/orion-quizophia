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

    // A teacher creates a new quiz
    public void createQuiz(Teacher teacher, Quiz newQuiz) {
        teacher.addCreatedQuiz(newQuiz.getQuizName()); // Using Name as ID for simplicity
        updateTeacherRecord(teacher);
        QuizFileManager quizMgr = new QuizFileManager("quizzes.txt");
        try {
            quizMgr.save(newQuiz);
        } catch (java.io.IOException e) {
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
