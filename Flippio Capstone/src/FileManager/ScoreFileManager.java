package FileManager;

import Model.*;

public class ScoreFileManager extends FileManager<QuizResult> {

    public ScoreFileManager(String fileName) {
        super(fileName);
    }

    @Override
    protected String convertToString(QuizResult result) {
        // Format: StudentID,QuizName,Score,TotalItems
        return result.getStudentId() + "," + result.getQuizName() + "," +
                result.getScore() + "," + result.getTotalItems();
    }

    @Override
    protected QuizResult parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) return null;

        try {
            return new QuizResult(parts[0], parts[1],
                    Double.parseDouble(parts[2]),
                    Integer.parseInt(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
