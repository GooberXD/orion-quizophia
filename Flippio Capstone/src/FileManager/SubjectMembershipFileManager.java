package FileManager;

import Exception.FileReadException;
import Exception.FileWriteException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores student IDs for a given subject. Now supports teacher-scoped subjects.
 * Falls back to legacy subject_<subjectId>.csv if no teacher ID provided.
 */
public class SubjectMembershipFileManager {
    private final File file;
    private final String teacherId;
    private final String subjectId;

    // Legacy constructor for backward compatibility
    public SubjectMembershipFileManager(String subjectId) {
        this.subjectId = subjectId;
        this.teacherId = null;
        this.file = new File("subject_" + subjectId + ".csv");
    }

    // New constructor with teacher scope
    public SubjectMembershipFileManager(String teacherId, String subjectId) {
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.file = null; // Not used when teacher ID is provided
    }

    public List<String> loadStudentIds() throws FileReadException {
        // If teacher ID provided, use new teacher-scoped storage
        if (teacherId != null) {
            try {
                TeacherSubjectFileManager tsfm = new TeacherSubjectFileManager(teacherId);
                TeacherSubjectFileManager.SubjectData subject = tsfm.getSubject(subjectId);
                return subject != null ? new ArrayList<>(subject.studentIds) : new ArrayList<>();
            } catch (FileReadException e) {
                return new ArrayList<>();
            }
        }
        
        // Legacy fallback
        List<String> ids = new ArrayList<>();
        if (!file.exists()) return ids;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) ids.add(line);
            }
            return ids;
        } catch (IOException e) {
            throw new FileReadException("Failed to read subject membership: " + file.getAbsolutePath());
        }
    }

    public void addStudentId(String studentId) throws FileWriteException, FileReadException {
        // If teacher ID provided, use new teacher-scoped storage
        if (teacherId != null) {
            try {
                TeacherSubjectFileManager tsfm = new TeacherSubjectFileManager(teacherId);
                tsfm.addStudentToSubject(subjectId, studentId);
                return;
            } catch (Exception e) {
                throw new FileWriteException("Failed to add student to teacher-scoped subject: " + e.getMessage());
            }
        }
        
        // Legacy fallback
        Set<String> ids = new HashSet<>(loadStudentIds());
        if (studentId != null && !studentId.trim().isEmpty()) {
            ids.add(studentId.trim());
        }
        overwrite(new ArrayList<>(ids));
    }

    public void removeStudentId(String studentId) throws FileWriteException, FileReadException {
        // If teacher ID provided, use new teacher-scoped storage
        if (teacherId != null) {
            try {
                TeacherSubjectFileManager tsfm = new TeacherSubjectFileManager(teacherId);
                tsfm.removeStudentFromSubject(subjectId, studentId);
                return;
            } catch (Exception e) {
                throw new FileWriteException("Failed to remove student from teacher-scoped subject: " + e.getMessage());
            }
        }
        
        // Legacy fallback
        List<String> ids = loadStudentIds();
        ids.removeIf(id -> id.equals(studentId));
        overwrite(ids);
    }

    private void overwrite(List<String> ids) throws FileWriteException {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                for (String id : ids) {
                    bw.write(id);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new FileWriteException("Failed to write subject membership: " + file.getAbsolutePath());
        }
    }
}
