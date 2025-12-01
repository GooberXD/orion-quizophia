package Service;

import Model.*;
import FileManager.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreService {
    private ScoreFileManager fileManager;
    private List<QuizResult> allResults;

    public ScoreService(String fileName) {
        this.fileManager = new ScoreFileManager(fileName);
        try {
            this.allResults = fileManager.load();
        } catch (IOException e) {
            this.allResults = new ArrayList<>();
            System.err.println("Could not load scores: " + e.getMessage());
        }
    }

    public void saveResult(QuizResult result) {
        allResults.add(result);
        try {
            fileManager.save(allResults);
        } catch (IOException e) {
            System.err.println("Failed to save result: " + e.getMessage());
        }
    }

    public List<QuizResult> getAllResults() {
        return allResults;
    }

    // Filter results for a specific student (Used in Student Dashboard)
    public List<QuizResult> getResultsByStudentId(String studentId) {
        return allResults.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    // Filter results for a specific Quiz (Used in Teacher Dashboard)
    public List<QuizResult> getResultsByQuizName(String quizName) {
        return allResults.stream()
                .filter(r -> r.getQuizName().equalsIgnoreCase(quizName))
                .collect(Collectors.toList());
    }
}
