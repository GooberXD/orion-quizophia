package FileManager;

import Exception.FileReadException;
import Exception.FileWriteException;
import Model.Quiz;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manages teacher-scoped subjects in a single file: teacher_<teacherId>.csv
 * Format: subjectId|subjectName|studentId1,studentId2,studentId3
 */
public class TeacherSubjectFileManager {
    private final File file;
    private final String teacherId;

    public TeacherSubjectFileManager(String teacherId) {
        this.teacherId = teacherId;
        this.file = new File("teacher_" + teacherId + ".csv");
    }

    /**
     * Load all subjects for this teacher
     * @return Map of subjectId -> SubjectData
     */
    public Map<String, SubjectData> loadAllSubjects() throws FileReadException {
        Map<String, SubjectData> subjects = new LinkedHashMap<>();
        if (!file.exists()) return subjects;
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|", 3);
                if (parts.length >= 2) {
                    String subjectId = parts[0];
                    String subjectName = parts[1];
                    List<String> studentIds = new ArrayList<>();
                    
                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        String[] ids = parts[2].split(",");
                        for (String id : ids) {
                            String trimmed = id.trim();
                            if (!trimmed.isEmpty()) {
                                studentIds.add(trimmed);
                            }
                        }
                    }
                    
                    subjects.put(subjectId, new SubjectData(subjectId, subjectName, studentIds));
                }
            }
            return subjects;
        } catch (IOException e) {
            throw new FileReadException("Failed to read teacher subjects: " + file.getAbsolutePath());
        }
    }

    /**
     * Get specific subject data
     */
    public SubjectData getSubject(String subjectId) throws FileReadException {
        return loadAllSubjects().get(subjectId);
    }

    /**
     * Create or update a subject
     */
    public void saveSubject(String subjectId, String subjectName, List<String> studentIds) throws FileReadException, FileWriteException {
        Map<String, SubjectData> subjects = loadAllSubjects();
        subjects.put(subjectId, new SubjectData(subjectId, subjectName, studentIds));
        saveAll(subjects);
    }

    /**
     * Add student to a subject
     */
    public void addStudentToSubject(String subjectId, String studentId) throws FileReadException, FileWriteException {
        Map<String, SubjectData> subjects = loadAllSubjects();
        SubjectData subject = subjects.get(subjectId);
        if (subject == null) {
            throw new FileReadException("Subject " + subjectId + " not found for teacher " + teacherId);
        }
        
        if (!subject.studentIds.contains(studentId)) {
            subject.studentIds.add(studentId);
            saveAll(subjects);

            // Auto-assign existing published quizzes in this subject to the new student with INC status
            assignIncToStudentForPublishedQuizzes(subjectId, studentId);
        }
    }

    /**
     * Remove student from a subject
     */
    public void removeStudentFromSubject(String subjectId, String studentId) throws FileReadException, FileWriteException {
        Map<String, SubjectData> subjects = loadAllSubjects();
        SubjectData subject = subjects.get(subjectId);
        if (subject != null) {
            subject.studentIds.remove(studentId);
            saveAll(subjects);
        }
    }

    // Ensure new students receive all published quizzes for the subject with INC status
    private void assignIncToStudentForPublishedQuizzes(String subjectId, String studentId) {
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            java.util.List<Quiz> quizzes = qfm.load();
            StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(studentId);
            java.util.List<String[]> existingRows = perfFm.readAll();

            for (Quiz q : quizzes) {
                boolean ownerOk = (q.getTeacherId() != null && teacherId != null && q.getTeacherId().equals(teacherId));
                boolean subjectOk = (q.getSubjectId() != null && q.getSubjectId().equals(subjectId));
                if (ownerOk && subjectOk && q.isPublished()) {
                    String quizName = q.getQuizName();
                    boolean alreadyHasEntry = false;
                    for (String[] row : existingRows) {
                        String rowSubject = (row.length >= 1 && row[0] != null) ? row[0] : "";
                        String rowQuiz = (row.length >= 2 && row[1] != null) ? row[1] : "";
                        if (rowQuiz.equalsIgnoreCase(quizName) && rowSubject.equalsIgnoreCase(subjectId)) {
                            alreadyHasEntry = true;
                            break;
                        }
                    }
                    if (!alreadyHasEntry) {
                        perfFm.appendRecord(subjectId, quizName, 0.0, "INC");
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Failed to assign INC entries for student " + studentId + " in subject " + subjectId + ": " + ex.getMessage());
        }
    }

    /**
     * Delete a subject - comprehensive cleanup including:
     * 1. Removes subject from teacher's subject list
     * 2. Removes all quizzes associated with the subject
     * 3. Removes subject's quiz entries from all student performance files
     * 4. Removes students from the subject membership
     */
    public void deleteSubject(String subjectId) throws FileReadException, FileWriteException {
        // Step 1: Remove subject from teacher's list
        Map<String, SubjectData> subjects = loadAllSubjects();
        SubjectData removedSubject = subjects.remove(subjectId);
        saveAll(subjects);
        
        // Step 2: Get list of students enrolled in this subject before deleting
        java.util.List<String> enrolledStudents = new java.util.ArrayList<>();
        if (removedSubject != null) {
            enrolledStudents.addAll(removedSubject.studentIds);
        }
        
        // Step 3: Remove all quizzes associated with this subject
        try {
            removeAllQuizzesForSubject(subjectId);
        } catch (Exception ex) {
            System.err.println("Warning: Failed to remove quizzes for subject " + subjectId + ": " + ex.getMessage());
        }
        
        // Step 4: Remove subject's quiz entries from student performance files
        try {
            removeSubjectFromStudentRecords(subjectId, enrolledStudents);
        } catch (Exception ex) {
            System.err.println("Warning: Failed to remove subject from student records: " + ex.getMessage());
        }
        
        // Step 5: Remove quiz index entries for this subject
        try {
            removeSubjectFromQuizIndex(subjectId);
        } catch (Exception ex) {
            System.err.println("Warning: Failed to remove subject from quiz index: " + ex.getMessage());
        }
    }
    
    // Helper: Remove all quizzes for a subject
    private void removeAllQuizzesForSubject(String subjectId) throws Exception {
        QuizFileManager qfm = new QuizFileManager("quizzes.txt");
        java.util.List<Quiz> allQuizzes = qfm.load();
        java.util.List<String> quizzesToDelete = new java.util.ArrayList<>();
        
        for (Quiz q : allQuizzes) {
            if (q.getSubjectId() != null && q.getSubjectId().equals(subjectId)) {
                quizzesToDelete.add(q.getQuizName());
            }
        }
        
        // Delete each quiz (which will also clean up student records)
        for (String quizName : quizzesToDelete) {
            try {
                qfm.deleteQuiz(quizName);
            } catch (Exception ex) {
                System.err.println("Error deleting quiz '" + quizName + "': " + ex.getMessage());
            }
        }
    }
    
    // Helper: Remove subject entries from all student performance records
    private void removeSubjectFromStudentRecords(String subjectId, java.util.List<String> enrolledStudents) throws java.io.IOException {
        for (String studentId : enrolledStudents) {
            try {
                removeSubjectFromStudentRecord(studentId, subjectId);
            } catch (Exception ex) {
                System.err.println("Error removing subject from student " + studentId + ": " + ex.getMessage());
            }
        }
    }
    
    // Helper: Remove subject lines from a single student's performance file
    private void removeSubjectFromStudentRecord(String studentId, String subjectId) throws java.io.IOException {
        java.io.File studentFile = new java.io.File("student_" + studentId + ".csv");
        if (!studentFile.exists()) return;
        
        java.util.List<String> filteredLines = new java.util.ArrayList<>();
        
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(studentFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                // Keep line if subject doesn't match
                if (parts.length < 1 || !parts[0].equalsIgnoreCase(subjectId)) {
                    filteredLines.add(line);
                }
            }
        }
        
        // Rewrite the file with filtered rows
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(studentFile, false))) {
            for (String line : filteredLines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
    
    // Helper: Remove subject from quiz index (quiz_9999.csv)
    private void removeSubjectFromQuizIndex(String subjectId) throws java.io.IOException {
        java.io.File indexFile = new java.io.File("quiz_9999.csv");
        if (!indexFile.exists()) return;
        
        java.util.List<String> lines = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(indexFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                // Keep line if subject doesn't match
                if (parts.length < 1 || !parts[0].equalsIgnoreCase(subjectId)) {
                    lines.add(line);
                }
            }
        }
        
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(indexFile, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    /**
     * Check if subject exists for this teacher
     */
    public boolean subjectExists(String subjectId) throws FileReadException {
        return loadAllSubjects().containsKey(subjectId);
    }

    private void saveAll(Map<String, SubjectData> subjects) throws FileWriteException {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                for (SubjectData subject : subjects.values()) {
                    bw.write(subject.subjectId);
                    bw.write("|");
                    bw.write(subject.subjectName);
                    bw.write("|");
                    bw.write(String.join(",", subject.studentIds));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new FileWriteException("Failed to write teacher subjects: " + file.getAbsolutePath());
        }
    }

    /**
     * Data class to hold subject information
     */
    public static class SubjectData {
        public final String subjectId;
        public final String subjectName;
        public final List<String> studentIds;

        public SubjectData(String subjectId, String subjectName, List<String> studentIds) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.studentIds = new ArrayList<>(studentIds);
        }

        public int getStudentCount() {
            return studentIds.size();
        }
    }
}
