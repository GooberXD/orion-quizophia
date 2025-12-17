package Controller;

import Model.*;
import View.*;
import Service.*;
import FileManager.*;
import Exception.*; // Custom Exceptions
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

public class StudentDashboardController {
    private StudentDashboardView view;
    private Student student;
    private String selectedSubjectId = null;

    public StudentDashboardController(StudentDashboardView view, Student student) {
        this.view = view;
        this.student = student;

        initDashboard();
    }

    private void initDashboard() {
        view.setStudentName(student.getName());
        loadSubjectsForStudent();
        loadPerformanceData(null);
        loadQuizButtons();
        view.addLogoutListener(e -> logout());
        view.addDownloadListener(e -> downloadReport());
        view.addSubjectSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedSubjectId = view.getSelectedSubjectId();
                loadQuizButtons();
                loadPerformanceData(selectedSubjectId);
                if (selectedSubjectId != null) { view.showDashboard(); }
            }
        });
        // Back button in dashboard returns to a Subjects panel
        view.addDashboardBackListener(e -> {
            selectedSubjectId = null;
            view.showSubjects();
            view.clearSubjectSelection();
            view.clearQuizButtons();
            loadPerformanceData(null);
        });
    }

    private void loadPerformanceData(String subjectFilterId) {
        // Load data from student-specific CSV file (student_<id>.csv)
        // Each row format: subjectId|quiz|percentage|status
        view.clearResultRows();
        double percentageSum = 0.0;
        int entries = 0;

        // 
        try {
            StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(student.getIdNumber());
            java.util.List<String[]> rows = perfFm.readAll();

            for (String[] row : rows) {
                if (row.length >= 3) {
                    String rowSubjectId = row[0] == null ? "" : row[0];
                    if (subjectFilterId != null && !subjectFilterId.isEmpty() && !subjectFilterId.equals(rowSubjectId)) {
                        continue; // skip non-matching subjects
                    }

                    String quizName = row.length > 1 && row[1] != null ? row[1] : "";
                    if (quizName.isEmpty()) continue; // skip invalid rows
                    boolean incomplete = false;
                    double percentage = 0.0;
                    try {
                        percentage = Double.parseDouble(row[2]);
                    } catch (Exception nfe) {
                        incomplete = true; // treat missing/invalid score as incomplete
                    }
                    
                    // Get an actual quiz to find total questions (match subject)
                    QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                    int totalQuestions = 0;
                    try {
                        for (Quiz q : qfm.load()) {
                            if (q.getQuizName().equalsIgnoreCase(quizName)) {
                                // Match quiz from the correct subject
                                if ((rowSubjectId == null || rowSubjectId.isEmpty()) && q.getSubjectId() == null) {
                                    totalQuestions = q.getQuestions().size();
                                    break;
                                } else if (rowSubjectId != null && !rowSubjectId.isEmpty() && rowSubjectId.equals(q.getSubjectId())) {
                                    totalQuestions = q.getQuestions().size();
                                    break;
                                }
                            }
                        }
                    } catch (Exception ignore) {}
                    
                    // Convert percentage back to actual score
                    double score = (totalQuestions == 0) ? 0.0 : (percentage / 100.0) * totalQuestions;
                    double displayScore = Math.round(score * 100.0) / 100.0; // clamp floating noise
                    // Read status from CSV if available (row[3]), otherwise calculate it
                    String status;
                    if (row.length >= 4 && row[3] != null && !row[3].trim().isEmpty()) {
                        String raw = row[3].trim();
                        if ("Completed".equalsIgnoreCase(raw)) {
                            // Map legacy "Completed" to PASS/FAIL based on percentage
                            status = (percentage >= 50.0) ? "PASS" : "FAIL";
                        } else {
                            status = raw;
                        }
                    } else {
                        status = incomplete ? "INC" : ((percentage >= 50.0) ? "PASS" : "FAIL");
                    }
                    view.addResultRow(quizName, displayScore, (double) totalQuestions, status);
                    percentageSum += percentage;
                    entries++;
                }
            }
        } catch (Exception ignored) {}

        if (entries == 0 && (subjectFilterId == null || subjectFilterId.isEmpty())) {
            // Fallback to in-memory results if no CSV entries yet (only when not filtering)
            java.util.List<Double> results = student.getQuizResults();
            int count = 1;
            for (Double score : results) {
                String quizName = "Quiz Attempt " + count;
                // Assume 10 items default for legacy results to estimate %
                double percent = (score / 10.0) * 100.0;
                String status = (percent >= 50.0) ? "PASSED" : "FAILED";
                double displayScore = Math.round(score * 100.0) / 100.0;
                view.addResultRow(quizName, displayScore, 10.0, status);
                percentageSum += percent;
                entries++;
                count++;
            }
        }

        

        double averagePercent = (entries == 0) ? 0.0 : (percentageSum / entries);
        view.updateAverageScore(averagePercent);
    }

    private void loadSubjectsForStudent() {
        try {
            java.util.List<Quiz> loadedQuizzes = new java.util.ArrayList<>();
            try {
                QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                loadedQuizzes = qfm.load();
            } catch (Exception ignore) {}

            java.util.Set<String> subjectIds = new java.util.HashSet<>();
            java.util.Map<String, String> subjectNames = new java.util.HashMap<>();
            java.util.Map<String, Integer> quizCounts = new java.util.HashMap<>();
            java.io.File cwd = new java.io.File(".");
            
            // Scan all teacher_*.csv files to find subjects where this student is enrolled
            java.io.File[] teacherFiles = cwd.listFiles((dir, name) -> name.startsWith("teacher_") && name.endsWith(".csv"));
            if (teacherFiles != null) {
                for (java.io.File teacherFile : teacherFiles) {
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(teacherFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty()) continue;
                            
                            // Format: subjectId|subjectName|studentId1,studentId2,studentId3
                            String[] parts = line.split("\\|", 3);
                            if (parts.length >= 3) {
                                String subjectId = parts[0];
                                String subjectName = parts[1];
                                String[] enrolledStudents = parts[2].split(",");
                                
                                // Check if this student is enrolled
                                for (String enrolledId : enrolledStudents) {
                                    if (enrolledId.trim().equals(student.getIdNumber())) {
                                        subjectIds.add(subjectId);
                                        subjectNames.put(subjectId, subjectName);
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception ignore) {}
                }
            }

            // Count only published quizzes not yet taken by the student for enrolled subjects
            for (Quiz q : loadedQuizzes) {
                if (q == null || !q.isPublished()) continue;
                String sid = q.getSubjectId() == null ? "" : q.getSubjectId();
                if (!subjectIds.contains(sid)) continue;
                if (hasTakenQuiz(q)) continue;
                quizCounts.put(sid, quizCounts.getOrDefault(sid, 0) + 1);
            }

            java.util.List<String[]> rows = new java.util.ArrayList<>();
            for (String sid : subjectIds) {
                String name = subjectNames.getOrDefault(sid, sid);
                int quizCount = quizCounts.getOrDefault(sid, 0);
                rows.add(new String[]{sid, name, String.valueOf(quizCount)});
            }
            view.setSubjectRows(rows);
            selectedSubjectId = null;
            view.clearSubjectSelection();
        } catch (Exception ex) {
            selectedSubjectId = null;
        }
    }

    private void loadQuizButtons() {
        view.clearQuizButtons();
        QuizFileManager quizManager = new QuizFileManager("quizzes.txt");
        List<Quiz> availableQuizzes = new ArrayList<>();

        try {
            availableQuizzes = quizManager.load();
        } catch (Exception e) {
            System.err.println("Could not load quizzes: " + e.getMessage());
        }

        // Show only quizzes that are published by teachers AND belong to subjects the student is enrolled in
        java.util.List<Quiz> visibleQuizzes = new java.util.ArrayList<>();
        // Build enrolled subject set by scanning all teacher_*.csv files
        java.util.Set<String> enrolledSubjectIds = new java.util.HashSet<>();
        try {
            java.io.File cwd = new java.io.File(".");
            java.io.File[] teacherFiles = cwd.listFiles((dir, name) -> name.startsWith("teacher_") && name.endsWith(".csv"));
            if (teacherFiles != null) {
                for (java.io.File teacherFile : teacherFiles) {
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(teacherFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty()) continue;
                            
                            // Format: subjectId|subjectName|studentId1,studentId2,studentId3
                            String[] parts = line.split("\\|", 3);
                            if (parts.length >= 3) {
                                String subjectId = parts[0];
                                String[] enrolledStudents = parts[2].split(",");
                                
                                // Check if this student is enrolled
                                for (String enrolledId : enrolledStudents) {
                                    if (enrolledId.trim().equals(student.getIdNumber())) {
                                        enrolledSubjectIds.add(subjectId);
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}

        for (Quiz q : availableQuizzes) {
            if (q != null && q.isPublished()) {
                String sid = q.getSubjectId();
                // If a quiz has a subjectId, require enrollment; if not, include it for backward compatibility
                boolean enrolled = (sid == null || sid.isEmpty()) || enrolledSubjectIds.contains(sid);
                boolean subjectMatches = (selectedSubjectId == null || selectedSubjectId.isEmpty()) || (sid != null && sid.equals(selectedSubjectId));
                if (enrolled && subjectMatches) {
                    visibleQuizzes.add(q);
                }
            }
        }

        if (!visibleQuizzes.isEmpty()) {
            for (Quiz q : visibleQuizzes) {
                boolean taken = hasTakenQuiz(q);
                String label = taken ? (q.getQuizName() + " (Taken)") : q.getQuizName();
                view.addQuizButton(label, e -> launchQuiz(q));
            }
            return;
        }
    }

    private void launchQuiz(Quiz quiz) {
        // Block retakes: a quiz can only be taken once per student
        if (hasTakenQuiz(quiz)) {
            JOptionPane.showMessageDialog(view, "You have already taken this quiz.");
            return;
        }
        QuizTakingView quizView = new QuizTakingView();
        new QuizTakingController(quizView, quiz, student);
        view.dispose();
        quizView.setVisible(true);
    }

    // Check student_<id>.csv to see if this quiz already has a recorded attempt
    // INC entries do NOT count as taken - only non-INC statuses block retakes
    private boolean hasTakenQuiz(Quiz quiz) {
        try {
            StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(student.getIdNumber());
            java.util.List<String[]> rows = perfFm.readAll();
            String targetName = quiz.getQuizName();
            String targetSubject = quiz.getSubjectId() == null ? "" : quiz.getSubjectId();
            for (String[] r : rows) {
                if (r.length >= 2) {
                    String recordedSubject = (r.length >= 1 && r[0] != null) ? r[0] : "";
                    String recordedQuiz = r[1] == null ? "" : r[1];
                    boolean nameMatch = recordedQuiz.equalsIgnoreCase(targetName);
                    boolean subjectMatch = targetSubject.isEmpty() || recordedSubject.equalsIgnoreCase(targetSubject) || recordedSubject.isEmpty();
                    if (nameMatch && subjectMatch) {
                        // Check the status column - only count as taken if the status is NOT "INC"
                        String status = (r.length >= 4 && r[3] != null) ? r[3].trim() : "";
                        if (!"INC".equalsIgnoreCase(status)) {
                            return true;  // Taken (has actual score)
                        }
                    }
                }
            }
        } catch (Exception ignore) { }
        return false;
    }

    // --- DOWNLOAD AND DISPLAY METHOD ---
    private void downloadReport() {
        StringBuilder reportBuilder = new StringBuilder();

        final String subjectFilterId = selectedSubjectId;

        // Read from student performance CSV (subject|quiz|percentage|status)
        double percentageSum = 0.0;
        int quizCount = 0;
        java.util.List<String> quizLines = new java.util.ArrayList<>();
        java.util.Set<String> recordedQuizNames = new java.util.HashSet<>();

        // Preload quizzes for the selected subject so we can show INC for unanswered
        java.util.Map<String, Integer> subjectQuizTotals = new java.util.HashMap<>();
        if (subjectFilterId != null && !subjectFilterId.isEmpty()) {
            try {
                QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                for (Quiz q : qfm.load()) {
                    if (q != null && subjectFilterId.equals(q.getSubjectId())) {
                        subjectQuizTotals.put(q.getQuizName(), (q.getQuestions() == null) ? 0 : q.getQuestions().size());
                    }
                }
            } catch (Exception ignore) {}
        }
        
        try {
            StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(student.getIdNumber());
            java.util.List<String[]> rows = perfFm.readAll();

            // Consolidate by quiz name, preferring non-INC over INC to reflect latest attempt
            java.util.Map<String, Object[]> quizMap = new java.util.LinkedHashMap<>(); // name -> {score(double), total(int), status(String), percentage(double)}

            for (String[] row : rows) {
                if (row.length >= 3) {
                    String rowSubjectId = row[0] == null ? "" : row[0];
                    if (subjectFilterId != null && !subjectFilterId.isEmpty() && !subjectFilterId.equals(rowSubjectId)) {
                        continue; // skip non-matching subjects
                    }

                    String quizName = (row.length > 1 && row[1] != null) ? row[1] : "";
                    if (quizName.isEmpty()) continue;

                    boolean incomplete = false;
                    double percentage = 0.0;
                    try {
                        percentage = Double.parseDouble(row[2]);
                    } catch (Exception nfe) {
                        incomplete = true; // treat missing/invalid score as incomplete
                    }

                    // Get actual quiz to find total questions (match subject)
                    QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                    int totalQuestions = 0;
                    try {
                        for (Quiz q : qfm.load()) {
                            if (q.getQuizName().equalsIgnoreCase(quizName)) {
                                boolean subjectMatches = (rowSubjectId == null || rowSubjectId.isEmpty()) ? (q.getSubjectId() == null) : rowSubjectId.equals(q.getSubjectId());
                                if (subjectMatches) {
                                    totalQuestions = q.getQuestions() == null ? 0 : q.getQuestions().size();
                                    break;
                                }
                            }
                        }
                    } catch (Exception ignore) {}

                    double score = (totalQuestions == 0) ? 0.0 : (percentage / 100.0) * totalQuestions;
                    String status;
                    if (row.length >= 4 && row[3] != null && !row[3].trim().isEmpty()) {
                        String raw = row[3].trim();
                        if ("Completed".equalsIgnoreCase(raw)) {
                            status = (percentage >= 50.0) ? "PASS" : "FAIL";
                        } else {
                            status = raw;
                        }
                    } else {
                        status = incomplete ? "INC" : ((percentage >= 50.0) ? "PASS" : "FAIL");
                    }

                    Object[] existing = quizMap.get(quizName);
                    boolean currentIsInc = "INC".equalsIgnoreCase(status);
                    boolean replace = (existing == null) || ("INC".equalsIgnoreCase((String) existing[2]) && !currentIsInc);
                    if (replace) {
                        quizMap.put(quizName, new Object[]{score, totalQuestions, status, percentage});
                    }
                }
            }

            for (java.util.Map.Entry<String, Object[]> entry : quizMap.entrySet()) {
                String quizName = entry.getKey();
                Object[] data = entry.getValue();
                double score = (double) data[0];
                int totalQuestions = (int) data[1];
                String status = (String) data[2];
                double percentage = (double) data[3];

                quizLines.add(String.format("- Quiz: %s | Score: %.1f / %d | Status: %s",
                        quizName, score, totalQuestions, status));
                percentageSum += percentage;
                quizCount++;
                recordedQuizNames.add(quizName);
            }
        } catch (Exception ex) {
            System.err.println("Failed to read performance data: " + ex.getMessage());
        }

        // Add INC lines for quizzes in the subject with no attempts recorded
        if (subjectFilterId != null && !subjectFilterId.isEmpty()) {
            for (java.util.Map.Entry<String, Integer> entry : subjectQuizTotals.entrySet()) {
                String quizName = entry.getKey();
                int totalQuestions = entry.getValue();
                if (recordedQuizNames.contains(quizName)) continue;
                String status = "INC";
                double score = 0.0;
                quizLines.add(String.format("- Quiz: %s | Score: %.1f / %d | Status: %s",
                        quizName, score, totalQuestions, status));
                // INC contributes 0 to average
                quizCount++;
            }
        }

        double avgPercent = (quizCount == 0) ? 0.0 : (percentageSum / quizCount);

        reportBuilder.append("FLIPPIO STUDENT REPORT\n");
        reportBuilder.append("----------------------\n");
        reportBuilder.append("Student: ").append(student.getName()).append("\n");
        reportBuilder.append("ID: ").append(student.getIdNumber()).append("\n");
        reportBuilder.append("Average Performance: ").append(String.format("%.2f%%", avgPercent)).append("\n");
        if (subjectFilterId != null && !subjectFilterId.isEmpty()) {
            reportBuilder.append("Subject Filter: ").append(subjectFilterId).append("\n");
        }
        reportBuilder.append("----------------------\n");
        reportBuilder.append("Quiz History:\n");

        if (!quizLines.isEmpty()) {
            for (String line : quizLines) {
                reportBuilder.append(line).append("\n");
            }
        } else {
            reportBuilder.append("No quizzes completed yet.\n");
        }

        String reportContent = reportBuilder.toString();
        String fileName = "Report_" + student.getName().replaceAll(" ", "_") + ".txt";

        // 2. Save to File
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {
            writer.write(reportContent);

            // Success Message
            JOptionPane.showMessageDialog(view, "Report downloaded successfully to: " + fileName);

            // 3. SHOW DIALOG (GUI Output)
            showReportDialog(reportContent);

        } catch (java.io.IOException e) {
            // Throw Custom Exception
            JOptionPane.showMessageDialog(view, new FileWriteException("Could not save report: " + e.getMessage()).getMessage());
        }
    }

    // Helper to show the report in a scrollable text area
    private void showReportDialog(String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));

        // Add vertical padding at both ends of content
        textArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        // Increase width so PASS/FAILED stays on one line
        scrollPane.setPreferredSize(new Dimension(600, 450));

        JOptionPane.showMessageDialog(view, scrollPane, "Grade Report Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        view.dispose();
        LoginView loginView = new LoginView();
        AuthService auth = AuthService.getInstance();
        new LoginController(loginView, auth);
        loginView.setVisible(true);
    }
}
