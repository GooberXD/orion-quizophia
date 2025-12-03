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

    // SAVING: We use a custom format to store the structure
    // Format: "QUIZ|Title" followed by "Q|QuestionText|Opt1|Opt2|Opt3|Opt4|AnsIndex"
    public void save(Quiz quiz) throws IOException {
        // Append to file (true) so we don't delete existing quizzes
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("QUIZ|" + quiz.getQuizName());
            writer.newLine();

            for (Question q : quiz.getQuestions()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Q|").append(q.getQuestionText()).append("|");

                // Add Choices
                for (String choice : q.getChoices()) {
                    sb.append(choice).append("|");
                }

                // Add Correct Answer Index
                sb.append(q.getCorrectAnswerIndex());

                writer.write(sb.toString());
                writer.newLine();
            }
            writer.write("END_QUIZ"); // Marker to know where it stops
            writer.newLine();
        }
    }

    // LOADING: Parses the custom format back into Objects
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
                    // Parse Question: Q | Text | Opt1 | Opt2 | Opt3 | Opt4 | Index
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