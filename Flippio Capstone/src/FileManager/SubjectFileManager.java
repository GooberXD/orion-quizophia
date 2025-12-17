package FileManager;

import Exception.FileReadException;
import Exception.FileWriteException;
import Model.Subject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubjectFileManager {
    private final File file;

    public SubjectFileManager(String filePath) {
        this.file = new File(filePath);
    }

    public List<Subject> readAll() throws FileReadException {
        List<Subject> list = new ArrayList<>();
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                list.add(Subject.fromCsvRow(line));
            }
            return list;
        } catch (IOException e) {
            throw new FileReadException("Failed to read subjects file: " + file.getAbsolutePath());
        }
    }

    public void writeAll(List<Subject> subjects) throws FileWriteException {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                for (Subject s : subjects) {
                    bw.write(s.toCsvRow());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new FileWriteException("Failed to write subjects file: " + file.getAbsolutePath());
        }
    }
}
