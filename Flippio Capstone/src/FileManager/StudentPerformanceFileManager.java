package FileManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Per-student performance CSV manager.
 * Format per line: <Subject Name>|<Quiz Name>|<Score>|<Status>
 */
public class StudentPerformanceFileManager {
    private final File file;

    public StudentPerformanceFileManager(String studentId) {
        this.file = new File("student_" + studentId + ".csv");
    }

    public void ensureExists() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                // create empty file
            }
        }
    }

    public void appendRecord(String subjectName, String quizName, double score, String status) throws IOException {
        ensureExists();
        String line = String.join("|",
                sanitize(subjectName),
                sanitize(quizName),
                String.valueOf(score),
                sanitize(status)
        );
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            bw.write(line);
            bw.newLine();
        }
    }

    public List<String[]> readAll() throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (!file.exists()) return rows;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                rows.add(parts);
            }
        }
        return rows;
    }

    private String sanitize(String s) { return s == null ? "" : s.replace("|", " "); }
}
