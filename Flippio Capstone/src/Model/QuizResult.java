package Model;

public class QuizResult {
    private String studentId;
    private String quizName;
    private double score;
    private int totalItems;

    public QuizResult(String studentId, String quizName, double score, int totalItems) {
        this.studentId = studentId;
        this.quizName = quizName;
        this.score = score;
        this.totalItems = totalItems;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getQuizName() { return quizName; }
    public double getScore() { return score; }
    public int getTotalItems() { return totalItems; }

    @Override
    public String toString() {
        return studentId + " - " + quizName + ": " + score;
    }
}
