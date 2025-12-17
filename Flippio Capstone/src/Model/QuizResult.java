package Model;

public class QuizResult {
    private final String studentId;
    private final String quizName;
    private final double score;
    private final int totalItems;

    public QuizResult(String studentId, String quizName, double score, int totalItems) {
        this.studentId = studentId;
        this.quizName = quizName;
        this.score = score;
        this.totalItems = totalItems;
    }

    public String getStudentId() { return studentId; }
    public String getQuizName() { return quizName; }
    public double getScore() { return score; }
    public int getTotalItems() { return totalItems; }

    // Calculation for percentage
    public double getPercentage() {
        if (totalItems == 0) return 0.0;
        return (score / totalItems) * 100;
    }
}