package Controller;

import FileManager.QuizFileManager;
import View.*;
import Model.*;
import Service.*;
import Utility.*;
import javax.swing.*;
import java.awt.*;

public class TeacherDashboardController {
    private TeacherDashboardView view;
    private Teacher teacher;
    private Subject subject; // optional subject context

    public TeacherDashboardController(TeacherDashboardView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;

        initController();
    }

    // Overload: with subject context (opened from Subject dashboard)
    public TeacherDashboardController(TeacherDashboardView view, Teacher teacher, Subject subject) {
        this.view = view;
        this.teacher = teacher;
        this.subject = subject;
        initController();
    }

    private void initController() {
        // 1. Set Welcome Text
        if (teacher != null) {
            view.setTeacherName(teacher.getName());
        }

        // 2. Load Subject Students and quizzes
        loadSubjectStudents();
        loadTeacherQuizzes();

        // 3. Navigation Listeners
        view.addLogoutListener(e -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView, AuthService.getInstance());
            PageNavigation.switchViews(view, loginView);
        });

        view.addCreateQuizListener(e -> {
            view.dispose(); // Close dashboard
            CreateQuizView createView = new CreateQuizView();
            if (subject != null) {
                new CreateQuizController(createView, teacher, subject);
            } else {
                new CreateQuizController(createView, teacher);
            }
            createView.setVisible(true);
        });

        // 4. Ranking Listener
        view.addRefreshRankListener(e -> calculateRankings());

        // 5. Delete Quiz Listener
        view.addDeleteQuizListener(e -> deleteQuizProcess());

        // 6. Back to Subjects (teacher-scoped)
        view.addBackListener(e -> {
            try {
                // Use teacher-scoped subject manager; falls back to blank view if unavailable
                TeacherSubjectDashboardView subjView = new TeacherSubjectDashboardView();
                FileManager.SubjectFileManager sfm = new FileManager.SubjectFileManager("subjects.csv");
                new SubjectController(sfm, subjView, teacher);
                subjView.setTitle("Teacher Subject Dashboard");
                PageNavigation.switchViews(view, subjView);
            } catch (Exception ex) {
                TeacherSubjectDashboardView subjView = new TeacherSubjectDashboardView();
                PageNavigation.switchViews(view, subjView);
            }
        });

        // 7. Add Student to Subject
        view.addAddStudentToSubjectListener(e -> addStudentToSubject());
        view.addDeleteStudentFromSubjectListener(e -> deleteStudentFromSubject());

        // 8. Quiz button pressed -> show modal with Modify/Turn In or performance
        // Modal is invoked from addQuizButton listeners created in loadTeacherQuizzes
    }

    private void loadTeacherQuizzes() {
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            java.util.List<Quiz> all = qfm.load();
            java.util.List<String> names = new java.util.ArrayList<>();
            for (Quiz q : all) {
                if (q == null) continue;
                boolean ownerOk = (q.getTeacherId() != null && teacher != null && q.getTeacherId().equals(teacher.getIdNumber()));
                boolean subjectOk = (subject == null || (q.getSubjectId() != null && q.getSubjectId().equals(subject.getId())));
                if (ownerOk && subjectOk) names.add(q.getQuizName());
            }
            // Populate buttons with proper listeners
            view.clearQuizButtons();
            for (String name : names) {
                view.addQuizButton(name, e -> showQuizModal(name));
            }
            // Ensure tile sizing recalculates after content changes
            view.refreshLayoutSizing();
        } catch (Exception ex) {
            System.err.println("Error loading quizzes: " + ex.getMessage());
            ex.printStackTrace();
            view.clearQuizButtons();
        }
    }

    private void showQuizModal(String quizName) {
        if (quizName == null || quizName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Invalid quiz name.");
            return;
        }
        
        // If quiz is published, show performance directly
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            java.util.List<Quiz> all = qfm.load();
            if (all != null) {
                for (Quiz q : all) {
                    if (q != null && q.getQuizName() != null && q.getQuizName().equalsIgnoreCase(quizName)) {
                        // Match quiz from the correct subject
                        if ((subject == null && q.getSubjectId() == null) || 
                            (subject != null && subject.getId() != null && subject.getId().equals(q.getSubjectId()))) {
                            if (q.isPublished()) { 
                                showPerformanceDialog(quizName, q.getSubjectId()); 
                                return; 
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error checking quiz published status: " + ex.getMessage());
        }

        JDialog dialog = new JDialog(view, quizName, true);
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        panel.setBackground(Color.WHITE);
        JLabel info = new JLabel("Quiz: " + quizName);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setFont(FontUtil.montserrat(16f, Font.BOLD, new Font("Arial", Font.BOLD, 16)));

        JButton modifyBtn = new RoundedButton("Modify");
        modifyBtn.setPreferredSize(new Dimension(140, 42));
        modifyBtn.setBackground(new Color(108, 117, 125)); // secondary gray
        modifyBtn.setForeground(Color.WHITE);
        modifyBtn.setFont(FontUtil.montserrat(14f, Font.BOLD, modifyBtn.getFont()));
        modifyBtn.addActionListener(e -> {
            dialog.dispose();
            // Dispose dashboard to avoid duplication and enable fresh reload after edit
            view.dispose();
            CreateQuizView editView = new CreateQuizView();
            if (subject != null) {
                new CreateQuizController(editView, teacher, subject, quizName);
            } else {
                new CreateQuizController(editView, teacher, null, quizName);
            }
            editView.setVisible(true);
        });

        JButton turnInBtn = new RoundedButton("Publish");
        turnInBtn.setPreferredSize(new Dimension(140, 42));
        turnInBtn.setBackground(new Color(40, 167, 69)); // green
        turnInBtn.setForeground(Color.WHITE);
        turnInBtn.setFont(FontUtil.montserrat(14f, Font.BOLD, turnInBtn.getFont()));
        turnInBtn.addActionListener(e -> {
            try {
                QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                // Find the correct quiz matching both name and subject
                String targetSubjectId = null;
                java.util.List<Quiz> all = qfm.load();
                for (Quiz q : all) {
                    if (q != null && q.getQuizName() != null && q.getQuizName().equalsIgnoreCase(quizName)) {
                        if ((subject == null && q.getSubjectId() == null) || 
                            (subject != null && subject.getId() != null && subject.getId().equals(q.getSubjectId()))) {
                            targetSubjectId = q.getSubjectId();
                            break;
                        }
                    }
                }
                if (targetSubjectId == null && subject != null) {
                    targetSubjectId = subject.getId();
                }
                
                // Publish quiz and get the list of students who received it
                java.util.List<String> studentsReceived = qfm.publishQuiz(quizName, targetSubjectId);
                
                // Display confirmation message with student list
                String messageText;
                if (studentsReceived.isEmpty()) {
                    messageText = "Quiz published successfully.\nNo students in this subject yet.";
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Quiz published successfully!\n\n");
                    sb.append("Students received the quiz with default INC status:\n");
                    sb.append("─".repeat(50)).append("\n");
                    for (String studentId : studentsReceived) {
                        sb.append("• ").append(studentId).append("\n");
                    }
                    sb.append("─".repeat(50)).append("\n");
                    sb.append("Total students: ").append(studentsReceived.size());
                    messageText = sb.toString();
                }
                
                JOptionPane.showMessageDialog(view, messageText, "Quiz Published", JOptionPane.INFORMATION_MESSAGE);
                // Reload quizzes and maintain tile sizing
                loadTeacherQuizzes();
                view.refreshLayoutSizing();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Failed to publish: " + ex.getMessage());
            } finally {
                dialog.dispose();
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        south.setBackground(Color.WHITE);
        // Rename button
        JButton renameBtn = new RoundedButton("Rename");
        renameBtn.setPreferredSize(new Dimension(140,42));
        renameBtn.setBackground(Color.decode("#c8abed"));
        renameBtn.setForeground(Color.BLACK);
        renameBtn.setFont(FontUtil.montserrat(14f, Font.BOLD, renameBtn.getFont()));
        renameBtn.addActionListener(ev -> {
            String newName = JOptionPane.showInputDialog(view, "Enter new quiz name:", quizName);
            if (newName == null || newName.trim().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(view,
                    "This will also update existing score records. Proceed?",
                    "Confirm Rename",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                qfm.renameQuiz(quizName, newName.trim());
                // Also rename scores entries
                ScoreService ss = new ScoreService("scores.csv");
                ss.renameQuizScores(quizName, newName.trim());
                JOptionPane.showMessageDialog(view, "Quiz renamed and scores updated.");
                dialog.dispose();
                loadTeacherQuizzes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Rename failed: " + ex.getMessage());
            }
        });

        south.add(modifyBtn);
        south.add(renameBtn);
        south.add(turnInBtn);
        panel.add(info, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setSize(700, 240);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    // Rounded button used for modal actions to match student style
    static class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2;
        public RoundedButton(String text){ super(text); setOpaque(false); setFocusPainted(false); setBorderPainted(false); }
        public void setRadius(int r){ radius=r; repaint(); }
        public void setBorderThickness(int t){ borderThickness=t; repaint(); }
        @Override protected void paintComponent(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillRoundRect(0,0,Math.abs(getWidth()-borderThickness),Math.abs(getHeight()-borderThickness),radius,radius); super.paintComponent(g); g2.dispose(); }
        @Override protected void paintBorder(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setStroke(new BasicStroke(borderThickness)); g2.setColor(Color.black); g2.drawRoundRect(1,1,Math.abs(getWidth()-borderThickness-1),Math.abs(getHeight()-borderThickness-1),radius,radius); g2.dispose(); }
    }

    // Show performance dialog when quiz is published
    private void showPerformanceDialog(String quizName, String contextSubjectId) {
        if (quizName == null || quizName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Invalid quiz name.");
            return;
        }
        
        // Use passed subject context
        String subjectId = contextSubjectId;
        int totalQuestions = 0;
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            java.util.List<Quiz> all = qfm.load();
            for (Quiz q : all) {
                if (q != null && q.getQuizName() != null && q.getQuizName().equalsIgnoreCase(quizName)) {
                    // Match quiz from the correct subject
                    if ((contextSubjectId == null && q.getSubjectId() == null) || 
                        (contextSubjectId != null && contextSubjectId.equals(q.getSubjectId()))) {
                        if (q.getQuestions() != null) {
                            totalQuestions = q.getQuestions().size();
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading quiz data: " + ex.getMessage());
            ex.printStackTrace();
        }

        // Build table of student performances for this quiz, filtered by subject membership,
        // pulling data from student_<id>.csv files (each row: subject|quiz|percentage|status)
        java.util.Set<String> memberIds = new java.util.HashSet<>();
        if (subjectId != null && !subjectId.trim().isEmpty()) {
            try {
                String teacherId = teacher != null ? teacher.getIdNumber() : null;
                FileManager.SubjectMembershipFileManager sm = new FileManager.SubjectMembershipFileManager(teacherId, subjectId);
                java.util.List<String> ids = sm.loadStudentIds();
                if (ids != null) {
                    memberIds.addAll(ids);
                }
            } catch (Exception ex) {
                System.err.println("Error loading subject membership: " + ex.getMessage());
            }
        }

        // If no subject membership was found (subjectId null or file missing), fall back to all students
        java.util.List<Student> registryStudents = new java.util.ArrayList<>();
        try {
            StudentService ss = new StudentService("students.csv");
            java.util.List<Student> allStudents = ss.getAllStudents();
            if (allStudents != null) registryStudents.addAll(allStudents);
            if (memberIds.isEmpty()) {
                for (Student s : registryStudents) {
                    if (s != null && s.getIdNumber() != null) memberIds.add(s.getIdNumber());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading all students: " + ex.getMessage());
        }

        // Ensure memberIds only contains currently-registered students (exclude deleted students)
        try {
            java.util.Set<String> registeredIds = new java.util.HashSet<>();
            for (Student s : registryStudents) if (s != null && s.getIdNumber() != null) registeredIds.add(s.getIdNumber());
            memberIds.removeIf(id -> !registeredIds.contains(id));
        } catch (Exception ex) {
            // If filtering fails, continue with whatever memberIds we have
        }

        String[] cols = {"Student ID", "Score", "Total", "Status"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0);

        for (String sid : memberIds) {
            if (sid == null || sid.trim().isEmpty()) continue;
            try {
                FileManager.StudentPerformanceFileManager pfm = new FileManager.StudentPerformanceFileManager(sid);
                java.util.List<String[]> rows = pfm.readAll();
                if (rows == null) continue;
                for (String[] row : rows) {
                    if (row == null || row.length < 3) continue;
                    String rowSubject = (row.length >= 1 && row[0] != null) ? row[0] : "";
                    String rowQuiz = row[1] == null ? "" : row[1];
                    if (rowQuiz.equalsIgnoreCase(quizName)) {
                        if (subjectId == null || subjectId.isEmpty() || rowSubject.equalsIgnoreCase(subjectId) || rowSubject.isEmpty()) {
                            String recordedStatus = (row.length >= 4 && row[3] != null) ? row[3].trim() : "";
                            
                            // Display students with INC status (not yet taken) with score 0
                            if ("INC".equalsIgnoreCase(recordedStatus)) {
                                model.addRow(new Object[]{sid, "0.0", totalQuestions, "INC"});
                            } else {
                                // For completed quizzes, show score and pass/fail status
                                double pct = 0.0;
                                try { pct = Double.parseDouble(row[2]); } catch (NumberFormatException nfe) { pct = 0.0; }
                                // Convert percentage back to actual score (e.g., 85.0% with 10 questions = 8.5 correct)
                                double score = (totalQuestions == 0) ? 0.0 : (pct / 100.0) * totalQuestions;
                                String status = (pct >= 50.0) ? "PASS" : "FAIL";
                                model.addRow(new Object[]{sid, String.format("%.1f", score), totalQuestions, status});
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error loading performance for student " + sid + ": " + ex.getMessage());
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(22);
        // Color status column: PASS (green), FAIL (red), INC (purple)
        table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer(){
            @Override protected void setValue(Object value){
                super.setValue(value);
                if ("PASS".equals(value)) {
                    setForeground(new Color(40,167,69)); // Green
                } else if ("FAIL".equals(value)) {
                    setForeground(new Color(200,50,50)); // Red
                } else if ("INC".equals(value)) {
                    setForeground(new Color(142,68,173)); // Purple
                }
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        JOptionPane.showMessageDialog(view, scroll, "Student Performance - " + quizName, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- LOGIC: DELETE QUIZ AND RECORDS ---
    private void deleteQuizProcess() {
        // 1. Ask user for the Quiz Name
        String quizName = JOptionPane.showInputDialog(view, "Enter the exact name of the Quiz to delete:");

        if (quizName != null && !quizName.trim().isEmpty()) {
            // Verify ownership/subject before confirming
            boolean allowed = false;
            try {
                QuizFileManager qfm = new QuizFileManager("quizzes.txt");
                for (Quiz q : qfm.load()) {
                    boolean ownerOk = (q.getTeacherId() != null && q.getTeacherId().equals(teacher.getIdNumber()));
                    boolean subjectOk = (subject == null || (q.getSubjectId() != null && q.getSubjectId().equals(subject.getId())));
                    if (q.getQuizName().equalsIgnoreCase(quizName.trim()) && ownerOk && subjectOk) {
                        allowed = true; break;
                    }
                }
            } catch (Exception ignore) {}

            if (!allowed) {
                JOptionPane.showMessageDialog(view, "You can only delete your own quiz in this context.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Are you sure you want to delete '" + quizName + "'?\nThis will delete the quiz AND all student results associated with it.\nThis cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // A. Delete the Quiz Definition (quizzes.txt)
                    QuizFileManager quizManager = new QuizFileManager("quizzes.txt");
                    quizManager.deleteQuiz(quizName.trim());

                    // B. Delete Related Scores - now stored in student_<id>.csv files (handled during student file cleanup)
                    // scoreService.deleteScoresByQuizName() no longer needed

                    // C. Refresh the Dashboard UI: reload tiles and recalc rankings
                    loadTeacherQuizzes();
                    view.refreshLayoutSizing();
                    calculateRankings(); // Recalculate ranks (averages might change)

                    JOptionPane.showMessageDialog(view, "Quiz and related records deleted successfully.");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
                }
            }
        }
    }

    // Removed loadStudentScores usage since Student Performance tab is removed.

    private void loadSubjectStudents() {
        try {
            if (subject == null) { 
                view.setStudents(java.util.Collections.emptyList()); 
                return; 
            }
            String teacherId = teacher != null ? teacher.getIdNumber() : null;
            FileManager.SubjectMembershipFileManager sm = new FileManager.SubjectMembershipFileManager(teacherId, subject.getId());
            java.util.List<String> ids = sm.loadStudentIds();
            // Build Student objects for display using students.csv only to resolve names
            StudentService studentService = new StudentService("students.csv");
            java.util.List<Student> all = studentService.getAllStudents();
            java.util.List<Student> members = new java.util.ArrayList<>();
            for (String id : ids) {
                if (id == null || id.trim().isEmpty()) continue;
                for (Student s : all) {
                    if (s != null && s.getIdNumber() != null && s.getIdNumber().equals(id)) { 
                        members.add(s); 
                        break; 
                    }
                }
            }
            // Sort by ID ascending (lexicographic; IDs are zero-padded)
            members.sort((a,b) -> {
                if (a == null || a.getIdNumber() == null) return 1;
                if (b == null || b.getIdNumber() == null) return -1;
                return a.getIdNumber().compareTo(b.getIdNumber());
            });
            view.setStudents(members);
        } catch (Exception ex) {
            System.err.println("Error loading subject students: " + ex.getMessage());
            ex.printStackTrace();
            view.setStudents(java.util.Collections.emptyList());
        }
    }

    private void addStudentToSubject() {
        if (subject == null) return;
        String id = javax.swing.JOptionPane.showInputDialog(view, "Enter existing Student ID to add:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            // Verify student exists
            StudentService studentService = new StudentService("students.csv");
            java.util.List<Student> all = studentService.getAllStudents();
            boolean exists = all.stream().anyMatch(s -> s.getIdNumber().equals(id.trim()));
            if (!exists) {
                javax.swing.JOptionPane.showMessageDialog(view, "Student ID not found.");
                return;
            }
            // Add to subject membership
            String teacherId = teacher != null ? teacher.getIdNumber() : null;
            FileManager.SubjectMembershipFileManager sm = new FileManager.SubjectMembershipFileManager(teacherId, subject.getId());
            sm.addStudentId(id.trim());

            // Also assign existing published quizzes in this subject to the newly added student with INC status
            try {
                FileManager.QuizFileManager qfm = new FileManager.QuizFileManager("quizzes.txt");
                java.util.List<Model.Quiz> quizzes = qfm.load();
                FileManager.StudentPerformanceFileManager perfFm = new FileManager.StudentPerformanceFileManager(id.trim());
                java.util.List<String[]> existingRows = perfFm.readAll();

                for (Model.Quiz q : quizzes) {
                    // Only consider quizzes owned by this teacher, tied to this subject, and already published
                    boolean ownerOk = (q.getTeacherId() != null && teacherId != null && q.getTeacherId().equals(teacherId));
                    boolean subjectOk = (q.getSubjectId() != null && q.getSubjectId().equals(subject.getId()));
                    if (ownerOk && subjectOk && q.isPublished()) {
                        String quizName = q.getQuizName();
                        boolean alreadyHasEntry = false;
                        for (String[] row : existingRows) {
                            String rowSubject = (row.length >= 1 && row[0] != null) ? row[0] : "";
                            String rowQuiz = (row.length >= 2 && row[1] != null) ? row[1] : "";
                            if (rowQuiz.equalsIgnoreCase(quizName) && rowSubject.equalsIgnoreCase(subject.getId())) {
                                alreadyHasEntry = true;
                                break;
                            }
                        }
                        if (!alreadyHasEntry) {
                            // Append INC entry: subjectId|quizName|0.0|INC
                            perfFm.appendRecord(subject.getId(), quizName, 0.0, "INC");
                        }
                    }
                }
            } catch (Exception incEx) {
                System.err.println("Failed to assign INC entries for existing quizzes to student " + id + ": " + incEx.getMessage());
                javax.swing.JOptionPane.showMessageDialog(view,
                        "Student added, but failed to assign existing quizzes (INC): " + incEx.getMessage());
            }

            loadSubjectStudents();
            updateSubjectEnrollmentCount();
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(view, "Failed to add student: " + ex.getMessage());
        }
    }

    private void deleteStudentFromSubject() {
        if (subject == null) return;
        // Prefer selected student in list
        Student selected = getSelectedStudentFromView();
        String id = selected != null ? selected.getIdNumber() : null;
        if (id == null) {
            id = javax.swing.JOptionPane.showInputDialog(view, "Enter Student ID to delete from subject:");
            if (id == null || id.trim().isEmpty()) return;
        }
        try {
            String teacherId = teacher != null ? teacher.getIdNumber() : null;

            // 1) Remove student from subject membership
            FileManager.SubjectMembershipFileManager sm = new FileManager.SubjectMembershipFileManager(teacherId, subject.getId());
            sm.removeStudentId(id.trim());

            // 2) Remove only the subject-related performance rows from student's file
            java.io.File perfFile = new java.io.File("student_" + id.trim() + ".csv");
            java.util.List<String> remainingLines = new java.util.ArrayList<>();
            java.util.List<String[]> removedRows = new java.util.ArrayList<>();
            if (perfFile.exists()) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(perfFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("\\|", -1);
                        String rowSubject = (parts.length >= 1 && parts[0] != null) ? parts[0] : "";
                        if (subject.getId() != null && !subject.getId().isEmpty() && rowSubject.equalsIgnoreCase(subject.getId())) {
                            // collect removed rows for later summary adjustments
                            removedRows.add(parts);
                        } else {
                            remainingLines.add(line);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Failed reading student performance file: " + ex.getMessage());
                }

                // overwrite with remaining lines (subject-specific rows removed)
                try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(perfFile, false))) {
                    for (String ln : remainingLines) { bw.write(ln); bw.newLine(); }
                } catch (Exception ex) {
                    System.err.println("Failed writing student performance file: " + ex.getMessage());
                }
            }

            // 3) For any removed row that was a completed quiz (status != INC), decrement teacher summary count
            if (!removedRows.isEmpty()) {
                try {
                    FileManager.QuizFileManager qfm = new FileManager.QuizFileManager("quizzes.txt");
                    java.util.List<Model.Quiz> allQuizzes = qfm.load();
                    for (String[] parts : removedRows) {
                        if (parts == null || parts.length < 4) continue;
                        String quizName = parts[1] == null ? "" : parts[1];
                        String status = parts[3] == null ? "" : parts[3];
                        if ("INC".equalsIgnoreCase(status)) continue; // skip INC entries

                        // find owning teacher for this quiz (match by name and subject)
                        String owningTeacherId = null;
                        String rowSubject = parts[0] == null ? "" : parts[0];
                        for (Model.Quiz q : allQuizzes) {
                            if (q == null || q.getQuizName() == null) continue;
                            boolean nameMatch = q.getQuizName().equalsIgnoreCase(quizName);
                            boolean subjectMatch = ((q.getSubjectId() == null || q.getSubjectId().isEmpty()) && (rowSubject == null || rowSubject.isEmpty())) ||
                                                   (q.getSubjectId() != null && q.getSubjectId().equalsIgnoreCase(rowSubject));
                            if (nameMatch && subjectMatch) { owningTeacherId = q.getTeacherId(); break; }
                        }

                        if (owningTeacherId != null && !owningTeacherId.trim().isEmpty()) {
                            try {
                                FileManager.TeacherQuizSummaryFileManager tqs = new FileManager.TeacherQuizSummaryFileManager(owningTeacherId);
                                java.util.List<String[]> rows = tqs.readAll();
                                boolean changed = false;
                                for (String[] r : rows) {
                                    String rsid = (r.length > 0 && r[0] != null) ? r[0] : "";
                                    String rquiz = (r.length > 1 && r[1] != null) ? r[1] : "";
                                    if ((rsid == null ? "" : rsid).equalsIgnoreCase(rowSubject == null ? "" : rowSubject) && rquiz.equalsIgnoreCase(quizName)) {
                                        int count = 0; try { count = Integer.parseInt(r.length > 2 && r[2] != null && !r[2].isEmpty() ? r[2] : "0"); } catch (NumberFormatException nfe) { count = 0; }
                                        if (count > 0) { r[2] = String.valueOf(count - 1); changed = true; }
                                    }
                                }
                                if (changed) tqs.writeAll(rows);
                            } catch (Exception ex) {
                                System.err.println("Failed updating teacher quiz summary: " + ex.getMessage());
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Failed adjusting teacher quiz summaries: " + ex.getMessage());
                }
            }

            // 4) Refresh UI
            loadSubjectStudents();
            updateSubjectEnrollmentCount();

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(view, "Failed to delete: " + ex.getMessage());
        }
    }

    private void updateSubjectEnrollmentCount() {
        
    }

    @SuppressWarnings("unchecked")
    private Student getSelectedStudentFromView() {
        // Try JTable selection first (new Students tab), then fallback to legacy JList
        try {
            java.lang.reflect.Field tableField = TeacherDashboardView.class.getDeclaredField("studentsTable");
            tableField.setAccessible(true);
            JTable table = (JTable) tableField.get(view);
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Assume model columns: 0 -> ID, 1 -> Name
                String id = String.valueOf(table.getValueAt(row, 0));
                String name = String.valueOf(table.getValueAt(row, 1));
                Student s = new Student(name, id, ""); // password not needed for display
                return s;
            }
        } catch (Exception ignore) { /* fall back */ }

        try {
            java.lang.reflect.Field listField = TeacherDashboardView.class.getDeclaredField("studentList");
            listField.setAccessible(true);
            JList<Student> list = (JList<Student>) listField.get(view);
            return list.getSelectedValue();
        } catch (Exception ignore) { return null; }
    }

    private void calculateRankings() {
        try {
            view.clearRankings();
            StudentService studentService = new StudentService("students.csv");
            java.util.List<Student> allStudents = studentService.getAllStudents();

            if (allStudents == null || allStudents.isEmpty()) {
                // If no students, just return, don't throw error to allow empty state
                return;
            }

            // If subject context exists, filter to only enrolled students
            java.util.List<Student> studentsToRank = new java.util.ArrayList<>();
            if (subject != null) {
                // Get enrolled students for this subject
                String teacherId = teacher != null ? teacher.getIdNumber() : null;
                FileManager.SubjectMembershipFileManager membershipFm = new FileManager.SubjectMembershipFileManager(teacherId, subject.getId());
                java.util.Set<String> enrolledStudentIds = new java.util.HashSet<>(membershipFm.loadStudentIds());
                for (Student s : allStudents) {
                    if (s != null && s.getIdNumber() != null && enrolledStudentIds.contains(s.getIdNumber())) {
                        studentsToRank.add(s);
                    }
                }
            } else {
                // No subject context, rank all students
                studentsToRank = new java.util.ArrayList<>(allStudents);
            }

            // Calculate average scores from student_<id>.csv files
            final String targetSubjectId = (subject != null) ? subject.getId() : null;
            java.util.List<StudentScore> studentScores = new java.util.ArrayList<>();
            for (Student s : studentsToRank) {
                if (s == null || s.getIdNumber() == null) continue;
                try {
                    FileManager.StudentPerformanceFileManager perfFm = new FileManager.StudentPerformanceFileManager(s.getIdNumber());
                    java.util.List<String[]> rows = perfFm.readAll();
                    double totalScore = 0.0;
                    int count = 0;
                    if (rows != null) {
                        for (String[] row : rows) {
                            if (row != null && row.length >= 3) {
                                // If subject context exists, only count scores for that subject (column 0 stores subjectId)
                                if (targetSubjectId != null) {
                                    String rowSubjectId = (row.length > 0) ? row[0] : "";
                                    if (!targetSubjectId.equals(rowSubjectId)) {
                                        continue; // Skip scores from other subjects
                                    }
                                }
                                try {
                                    totalScore += Double.parseDouble(row[2]);
                                    count++;
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                    double averageScore = (count > 0) ? (totalScore / count) : 0.0;
                    studentScores.add(new StudentScore(s.getName(), averageScore));
                } catch (Exception ex) {
                    // Skip this student if there's an error reading their performance
                    System.err.println("Error reading performance for student " + s.getIdNumber() + ": " + ex.getMessage());
                }
            }

            // Sort by Average Score (Descending)
            studentScores.sort((s1, s2) -> Double.compare(s2.averageScore, s1.averageScore));

            int rank = 1;
            for (StudentScore ss : studentScores) {
                if (ss != null && ss.name != null) {
                    view.addRankingRow(rank++, ss.name, ss.averageScore);
                }
            }

        } catch (Exception e) {
            // Ignore file errors during refresh
            System.err.println("Error calculating rankings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper class to hold student name and average score
    private static class StudentScore {
        String name;
        double averageScore;
        StudentScore(String name, double averageScore) {
            this.name = name;
            this.averageScore = averageScore;
        }
    }
}
