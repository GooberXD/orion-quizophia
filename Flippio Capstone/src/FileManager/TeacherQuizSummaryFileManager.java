package FileManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Per-teacher quiz summary CSV.
 * Format per line: <Subject ID>|<Quiz Name>|<Number of Student takes the Quiz>
 */
public class TeacherQuizSummaryFileManager {
    private final File file;

    public TeacherQuizSummaryFileManager(String teacherId) {
        this.file = new File("quiz_" + teacherId + ".csv");
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

    public void writeAll(List<String[]> rows) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            for (String[] r : rows) {
                String sid = safe(r,0), quiz = safe(r,1), count = safe(r,2);
                bw.write(String.join("|", sid, quiz, count));
                bw.newLine();
            }
        }
    }

    private String safe(String[] a, int i){ return (a!=null && a.length>i && a[i]!=null) ? a[i] : ""; }
}
