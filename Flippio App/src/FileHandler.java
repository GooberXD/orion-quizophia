import java.io.*;
import java.util.*;

public class FileHandler {
    private static final String USER_FILE = "users.txt";
    private static final String QUESTION_FILE = "questions.txt";
    private static final String SCORE_FILE = "scores.txt";

    private QuestionFactory questionFactory;

    public FileHandler() {
        this.questionFactory = new QuestionFactory();
        initializeFiles();
    }

    private void initializeFiles() {
        try {
            new File(USER_FILE).createNewFile();
            new File(QUESTION_FILE).createNewFile();
            new File(SCORE_FILE).createNewFile();
        } catch (IOException e) {
            System.out.println("Error initializing files: " + e.getMessage());
        }
    }

    // ==========================================
    // 1. USER HANDLING (Teachers & Students)
    // ==========================================

    public void saveUser(User p) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            String type = (p instanceof Teacher) ? "TEACHER" : "STUDENT";
            String line = type + "|" + p.getId() + "|" + p.getName();
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> people = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;

                String type = parts[0];
                String id = parts[1];
                String name = parts[2];

                if (type.equals("TEACHER")) {
                    people.add(new Teacher(id, name));
                } else if (type.equals("STUDENT")) {
                    people.add(new Student(id, name));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return people;
    }

    // ==========================================
    // 2. QUESTION & SUBJECT HANDLING
    // ==========================================

    public void saveSubjects(List<Subject> subjects) {
        // Overwrites the file to keep it updated
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTION_FILE))) {
            for (Subject sub : subjects) {
                for (Question q : sub.getQuestions()) {
                    if (q instanceof MultipleChoiceQuestion) {
                        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
                        String[] choices = mcq.getChoices();

                        // Construct the line
                        // Format: SUBJECT|MC|Text|C1|C2|C3|C4|Ans
                        StringBuilder line = new StringBuilder();
                        line.append(sub.getSubjectName()).append("|");
                        line.append("MC").append("|");
                        line.append(mcq.getQuestionText()).append("|");
                        line.append(choices[0]).append("|");
                        line.append(choices[1]).append("|");
                        line.append(choices[2]).append("|");
                        line.append(choices[3]).append("|");
                        line.append(mcq.getCorrectAnswer());

                        writer.write(line.toString());
                        writer.newLine();
                    }
                }
            }
            System.out.println("All questions saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }

    public List<Subject> loadSubjects() {
        Map<String, Subject> subjectMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 8) continue; // Skip invalid lines

                String subjectName = parts[0];
                String type = parts[1];
                String factoryData = line.substring(line.indexOf("|") + 1);
                Question q = questionFactory.createQuestion(type, factoryData);

                if (q != null) {
                    subjectMap.putIfAbsent(subjectName, new Subject(subjectName));
                    subjectMap.get(subjectName).addQuestion(q);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }

        return new ArrayList<>(subjectMap.values());
    }

    // ==========================================
    // 3. SCORE HANDLING
    // ==========================================

    public void saveScore(String studentId, String subjectName, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(studentId + "|" + subjectName + "|" + score);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }
}