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

    public String getStudentId() { return studentId; }
    public String getQuizName() { return quizName; }
    public double getScore() { return score; }
    public int getTotalItems() { return totalItems; }

    // Optional: Calculation for percentage
    public double getPercentage() {
        if (totalItems == 0) return 0.0;
        return (score / totalItems) * 100;
    }
}