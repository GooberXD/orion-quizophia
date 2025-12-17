package Model;

import java.util.*;

public class Student extends User {
    private List<Double> quizResults;

    public Student(String idNumber, String password, String name) {
        super(idNumber, password, name);
        this.quizResults = new ArrayList<>();
    }

    // Method to add a new score (Called by QuizTakingController)
    public void addQuizResult(double score) {
        this.quizResults.add(score);
    }

    // Method to retrieve scores (Used for the Dashboard/File Saving)
    public List<Double> getQuizResults() {
        return this.quizResults;
    }

    // Calculate Average for the Dashboard
    public double getAverageScore() {
        if (quizResults.isEmpty()) return 0.0;
        double sum = 0;
        for (Double score : quizResults) {
            sum += score;
        }
        return sum / quizResults.size();
    }

    @Override
    public String toString() {
        return getName() + " (" + getIdNumber() + ")";
    }
}
