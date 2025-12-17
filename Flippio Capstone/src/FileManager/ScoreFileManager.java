package FileManager;

import Model.QuizResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreFileManager {
    private String filename;

    public ScoreFileManager(String filename) {
        this.filename = filename;
    }

    public List<QuizResult> load() throws IOException {
        List<QuizResult> results = new ArrayList<>();
        File file = new File(filename);
        if(!file.exists()) return results;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: studentId,quizName,score,totalItems
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String sId = parts[0];
                        String qName = parts[1];
                        double score = Double.parseDouble(parts[2]);
                        int total = Integer.parseInt(parts[3]);

                        results.add(new QuizResult(sId, qName, score, total));
                    } catch (NumberFormatException e) {
                        // Skip malformed lines
                    }
                }
            }
        }
        return results;
    }

    // --- USED BY saveResult (Appends to file) ---
    public void save(QuizResult result) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) { // true = append
            writer.write(result.getStudentId() + "," + result.getQuizName() + "," + result.getScore() + "," + result.getTotalItems());
            writer.newLine();
        }
    }

    // --- USED BY deleteScoresByQuizName (Overwrites file) ---
    public void overwrite(List<QuizResult> results) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) { // false = overwrite
            for (QuizResult r : results) {
                writer.write(r.getStudentId() + "," + r.getQuizName() + "," + r.getScore() + "," + r.getTotalItems());
                writer.newLine();
            }
        }
    }
}