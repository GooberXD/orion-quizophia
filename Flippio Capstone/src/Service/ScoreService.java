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

    public void saveResult(QuizResult result) throws Exception {
        fileManager.save(result);
    }

    // Delete method
    public void deleteScoresByQuizName(String quizName) throws Exception {
        List<QuizResult> allResults = getAllResults();
        allResults.removeIf(r -> r.getQuizName().equalsIgnoreCase(quizName));
        fileManager.overwrite(allResults);
    }

    public void renameQuizScores(String oldName, String newName) throws Exception {
        if (newName == null || newName.trim().isEmpty()) throw new Exception("New quiz name cannot be empty.");
        List<QuizResult> all = getAllResults();
        for (QuizResult r : all) {
            if (r.getQuizName().equalsIgnoreCase(oldName)) {
                QuizResult updated = new QuizResult(r.getStudentId(), newName.trim(), r.getScore(), r.getTotalItems());
                int idx = all.indexOf(r);
                all.set(idx, updated);
            }
        }
        fileManager.overwrite(all);
    }
}