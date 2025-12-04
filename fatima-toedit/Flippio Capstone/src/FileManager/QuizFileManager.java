package FileManager;

import Model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFileManager {
    private String fileName;

    public QuizFileManager(String fileName) {
        this.fileName = fileName;
    }

    // EXISTING SAVE (Appends one quiz)
    public void save(Quiz quiz) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writeQuizToBuffer(writer, quiz);
        }
    }

    // --- NEW METHOD: DELETE QUIZ ---
    public void deleteQuiz(String quizName) throws Exception {
        // 1. Load all existing quizzes
        List<Quiz> allQuizzes = load();

        // 2. Remove the one matching the name
        boolean removed = allQuizzes.removeIf(q -> q.getQuizName().equalsIgnoreCase(quizName));

        if (!removed) {
            throw new Exception("Quiz '" + quizName + "' not found.");
        }

        // 3. Overwrite the file with the remaining quizzes
        saveAll(allQuizzes);
    }

    // --- HELPER: OVERWRITE FILE ---
    private void saveAll(List<Quiz> quizzes) throws IOException {
        // "false" in FileWriter means overwrite, not append
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (Quiz q : quizzes) {
                writeQuizToBuffer(writer, q);
            }
        }
    }

    // --- HELPER: FORMATTING LOGIC ---
    // Extracted this logic so it can be used by both save() and saveAll()
    private void writeQuizToBuffer(BufferedWriter writer, Quiz quiz) throws IOException {
        writer.write("QUIZ|" + quiz.getQuizName());
        writer.newLine();

        for (Question q : quiz.getQuestions()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Q|").append(q.getQuestionText()).append("|");

            for (String choice : q.getChoices()) {
                sb.append(choice).append("|");
            }

            sb.append(q.getCorrectAnswerIndex());

            writer.write(sb.toString());
            writer.newLine();
        }
        writer.write("END_QUIZ");
        writer.newLine();
    }

    // EXISTING LOAD METHOD (Unchanged)
    public List<Quiz> load() throws IOException {
        List<Quiz> quizzes = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return quizzes;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Quiz currentQuiz = null;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (line.startsWith("QUIZ|")) {
                    currentQuiz = new Quiz(parts[1]);
                } else if (line.startsWith("Q|") && currentQuiz != null) {
                    String text = parts[1];
                    List<String> choices = new ArrayList<>();
                    choices.add(parts[2]);
                    choices.add(parts[3]);
                    choices.add(parts[4]);
                    choices.add(parts[5]);
                    int correctIndex = Integer.parseInt(parts[6]);

                    currentQuiz.addQuestion(new Question(text, choices, correctIndex));
                } else if (line.equals("END_QUIZ")) {
                    if (currentQuiz != null) {
                        quizzes.add(currentQuiz);
                        currentQuiz = null;
                    }
                }
            }
        }
        return quizzes;
    }
}