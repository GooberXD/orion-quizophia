package Service;

import FileManager.ScoreFileManager;
import Model.QuizResult;
import java.util.List;

public class ScoreService {
    private ScoreFileManager fileManager;

    public ScoreService(String filename) {
        this.fileManager = new ScoreFileManager(filename);
    }

    public List<QuizResult> getAllResults() {
        try {
            return fileManager.load();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    // --- THIS IS THE MISSING METHOD CAUSING YOUR ERROR ---
    public void saveResult(QuizResult result) throws Exception {
        fileManager.save(result);
    }

    // Delete method (from previous step)
    public void deleteScoresByQuizName(String quizName) throws Exception {
        List<QuizResult> allResults = getAllResults();
        boolean removed = allResults.removeIf(r -> r.getQuizName().equalsIgnoreCase(quizName));
        fileManager.overwrite(allResults);
    }
}