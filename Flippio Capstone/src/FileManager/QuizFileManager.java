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
    /**
     * Deletes a quiz from the system, including:
     * 1. Removes from quizzes.txt
     * 2. Removes from all student performance files (student_<id>.csv)
     * 3. Removes from quiz index (quiz_9999.csv)
     */
    public void deleteQuiz(String quizName) throws Exception {
        // 1. Load all existing quizzes
        List<Quiz> allQuizzes = load();

        // 2. Remove the one matching the name and get subject info for cleanup
        String subjectId = null;
        boolean removed = false;
        for (Quiz q : allQuizzes) {
            if (q.getQuizName().equalsIgnoreCase(quizName)) {
                subjectId = q.getSubjectId();
                removed = true;
                break;
            }
        }
        
        if (!removed) {
            throw new Exception("Quiz '" + quizName + "' not found.");
        }
        
        allQuizzes.removeIf(q -> q.getQuizName().equalsIgnoreCase(quizName));

        // 3. Overwrite the file with the remaining quizzes
        saveAll(allQuizzes);
        
        // 4. Remove quiz entries from all student performance files
        try {
            removeQuizFromAllStudentRecords(quizName);
        } catch (Exception ex) {
            System.err.println("Warning: Failed to remove quiz from some student records: " + ex.getMessage());
        }
        
        // 5. Remove from quiz index (quiz_9999.csv) if it has a subject
        if (subjectId != null && !subjectId.isEmpty()) {
            try {
                removeQuizFromIndex(quizName, subjectId);
            } catch (Exception ex) {
                System.err.println("Warning: Failed to remove quiz from index: " + ex.getMessage());
            }
        }
    }
    
    // Helper method to remove quiz from all student performance records
    private void removeQuizFromAllStudentRecords(String quizName) throws Exception {
        java.io.File dir = new java.io.File(".");
        java.io.File[] files = dir.listFiles((d, name) -> name.startsWith("student_") && name.endsWith(".csv"));
        
        if (files == null) return;
        
        for (java.io.File studentFile : files) {
            try {
                removeQuizFromStudentRecord(studentFile, quizName);
            } catch (Exception ex) {
                System.err.println("Error removing quiz from " + studentFile.getName() + ": " + ex.getMessage());
            }
        }
    }
    
    // Helper method to remove quiz from a single student's performance file
    private void removeQuizFromStudentRecord(java.io.File studentFile, String quizName) throws java.io.IOException {
        java.util.List<String> allLines = new java.util.ArrayList<>();
        
        // Read all lines, filtering out the deleted quiz
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                // Keep line if quiz name doesn't match
                if (parts.length < 2 || !parts[1].equalsIgnoreCase(quizName)) {
                    allLines.add(line);
                }
            }
        }
        
        // Write back without the deleted quiz
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(studentFile, false))) {
            for (String line : allLines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
    
    // Helper method to remove quiz from index file (quiz_9999.csv)
    private void removeQuizFromIndex(String quizName, String subjectId) throws java.io.IOException {
        java.io.File indexFile = new java.io.File("quiz_9999.csv");
        if (!indexFile.exists()) return;
        
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(indexFile))) {
            String line;
            String targetPrefix = subjectId + "|" + quizName + "|";
            while ((line = br.readLine()) != null) {
                // Keep line if it doesn't match the deleted quiz
                if (!line.startsWith(targetPrefix)) {
                    lines.add(line);
                }
            }
        }
        
        // Write back without the deleted quiz
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(indexFile, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
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
        // Optional metadata line for teacher and subject
        {
            String t = quiz.getTeacherId() == null ? "" : quiz.getTeacherId();
            String s = quiz.getSubjectId() == null ? "" : quiz.getSubjectId();
            String p = Boolean.toString(quiz.isPublished());
            writer.write("META|" + t + "|" + s + "|" + p);
            writer.newLine();
        }

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
            boolean expectingMeta = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (line.startsWith("QUIZ|")) {
                    currentQuiz = new Quiz(parts[1]);
                    expectingMeta = true; // next line may be META or Q
                } else if (expectingMeta && line.startsWith("META|") && currentQuiz != null) {
                    String teacherId = parts.length > 1 ? parts[1] : "";
                    String subjectId = parts.length > 2 ? parts[2] : "";
                    String published = parts.length > 3 ? parts[3] : "false";
                    currentQuiz.setTeacherId(teacherId.isEmpty() ? null : teacherId);
                    currentQuiz.setSubjectId(subjectId.isEmpty() ? null : subjectId);
                    currentQuiz.setPublished(Boolean.parseBoolean(published));
                    expectingMeta = false;
                } else if (line.startsWith("Q|") && currentQuiz != null) {
                    expectingMeta = false; // metadata absent
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
                        expectingMeta = false;
                    }
                }
            }
        }
        return quizzes;
    }

    // Update an existing quiz by name
    public void updateQuiz(Quiz updated) throws IOException, Exception {
        List<Quiz> all = load();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getQuizName().equalsIgnoreCase(updated.getQuizName())) {
                all.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) throw new Exception("Quiz '" + updated.getQuizName() + "' not found to update.");
        saveAll(all);
    }

    // Mark a quiz as published and return list of students who received it
    public java.util.List<String> publishQuiz(String quizName) throws IOException, Exception {
        return publishQuiz(quizName, null);
    }

    /**
     * Publishes a quiz and creates INC (incomplete) entries for all enrolled students.
     * @param quizName Name of the quiz to publish
     * @param subjectId Subject ID (optional - can be null)
     * @return List of student IDs who received the quiz with default INC status and score 0
     */
    public java.util.List<String> publishQuiz(String quizName, String subjectId) throws IOException, Exception {
        List<Quiz> all = load();
        Quiz publishedQuiz = null;
        boolean found = false;
        for (Quiz q : all) {
            if (q.getQuizName().equalsIgnoreCase(quizName)) {
                // Match subject if provided
                if ((subjectId == null && q.getSubjectId() == null) || 
                    (subjectId != null && subjectId.equals(q.getSubjectId()))) {
                    q.setPublished(true);
                    publishedQuiz = q;
                    found = true;
                    break;
                }
            }
        }
        if (!found) throw new Exception("Quiz '" + quizName + "' not found to publish.");
        saveAll(all);

        // Track all students who receive this quiz
        java.util.List<String> studentsReceivedQuiz = new java.util.ArrayList<>();

        // Auto-create INC entries for enrolled students
        if (publishedQuiz != null && publishedQuiz.getSubjectId() != null && !publishedQuiz.getSubjectId().isEmpty()) {
            try {
                String teacherId = publishedQuiz.getTeacherId();
                SubjectMembershipFileManager membershipFm = new SubjectMembershipFileManager(teacherId, publishedQuiz.getSubjectId());
                java.util.List<String> enrolledIds = membershipFm.loadStudentIds();
                
                for (String studentId : enrolledIds) {
                    try {
                        StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(studentId);
                        // Check if student already has an entry for this quiz
                        boolean alreadyHasEntry = false;
                        java.util.List<String[]> rows = perfFm.readAll();
                        for (String[] row : rows) {
                            if (row.length >= 2 && row[1] != null && row[1].equalsIgnoreCase(quizName)) {
                                alreadyHasEntry = true;
                                break;
                            }
                        }
                        if (!alreadyHasEntry) {
                            // Append INC entry: subjectId|quizName|0.0|INC
                            perfFm.appendRecord(publishedQuiz.getSubjectId(), quizName, 0.0, "INC");
                            studentsReceivedQuiz.add(studentId);
                        }
                    } catch (Exception ex) {
                        System.err.println("Failed to create INC entry for student " + studentId + ": " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to load enrolled students for subject " + publishedQuiz.getSubjectId() + ": " + ex.getMessage());
            }

            // Upsert subject quiz index entry: subjectId|quizName|totalQuestions into quiz_9999.csv
            try {
                java.io.File indexFile = new java.io.File("quiz_9999.csv");
                java.util.List<String> lines = new java.util.ArrayList<>();
                if (indexFile.exists()) {
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(indexFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                        }
                    }
                }
                String targetPrefix = publishedQuiz.getSubjectId() + "|" + quizName + "|";
                String newLine = publishedQuiz.getSubjectId() + "|" + quizName + "|" + (publishedQuiz.getQuestions() == null ? 0 : publishedQuiz.getQuestions().size());
                boolean replaced = false;
                for (int i = 0; i < lines.size(); i++) {
                    String l = lines.get(i);
                    if (l != null && l.startsWith(targetPrefix)) {
                        lines.set(i, newLine);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    lines.add(newLine);
                }
                try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(indexFile, false))) {
                    for (String l : lines) {
                        bw.write(l == null ? "" : l);
                        bw.newLine();
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to upsert quiz index for subject " + publishedQuiz.getSubjectId() + ": " + ex.getMessage());
            }
        }
        
        return studentsReceivedQuiz;
    }

    // Rename a quiz (ensures no duplicate name)
    public void renameQuiz(String oldName, String newName) throws IOException, Exception {
        if (newName == null || newName.trim().isEmpty()) throw new Exception("New name cannot be empty.");
        List<Quiz> all = load();
        // Check duplicate
        for (Quiz q : all) {
            if (q.getQuizName().equalsIgnoreCase(newName.trim())) {
                throw new Exception("A quiz named '" + newName + "' already exists.");
            }
        }
        boolean found = false;
        for (Quiz q : all) {
            if (q.getQuizName().equalsIgnoreCase(oldName)) {
                q.setQuizName(newName.trim());
                found = true;
                break;
            }
        }
        if (!found) throw new Exception("Quiz '" + oldName + "' not found to rename.");
        saveAll(all);
    }

    // Repair subject quiz index: scan quizzes.txt for published quizzes and upsert missing entries into quiz_9999.csv
    public static void repairSubjectQuizIndex() {
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            List<Quiz> all = qfm.load();
            
            java.io.File indexFile = new java.io.File("quiz_9999.csv");
            java.util.List<String> lines = new java.util.ArrayList<>();
            
            // Load existing index
            if (indexFile.exists()) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(indexFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().length() > 0) {
                            lines.add(line);
                        }
                    }
                }
            }
            
            boolean modified = false;
            
            // Scan all quizzes for published entries with subject
            for (Quiz q : all) {
                if (q.isPublished() && q.getSubjectId() != null && !q.getSubjectId().isEmpty()) {
                    String targetPrefix = q.getSubjectId() + "|" + q.getQuizName() + "|";
                    String newLine = q.getSubjectId() + "|" + q.getQuizName() + "|" + (q.getQuestions() == null ? 0 : q.getQuestions().size());
                    
                    boolean found = false;
                    for (String l : lines) {
                        if (l != null && l.startsWith(targetPrefix)) {
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        lines.add(newLine);
                        modified = true;
                        System.out.println("Added missing quiz index entry: " + newLine);
                    }
                }
            }
            
            // Write back if modified
            if (modified) {
                try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(indexFile, false))) {
                    for (String l : lines) {
                        bw.write(l == null ? "" : l);
                        bw.newLine();
                    }
                }
                System.out.println("Subject quiz index repaired successfully.");
            }
        } catch (Exception ex) {
            System.err.println("Failed to repair subject quiz index: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
