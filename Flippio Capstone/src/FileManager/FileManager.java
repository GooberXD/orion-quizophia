package FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Uses Generics <T> as per your class diagram references to List<Student>, List<Quiz>
public abstract class FileManager<T> {
    protected String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    // TEMPLATE METHOD: Handles the IO logic (File Handling requirement)
    public void save(List<T> dataList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (T data : dataList) {
                writer.write(convertToString(data)); // Delegate to subclass
                writer.newLine();
            }
        }
    }

    // TEMPLATE METHOD: Handles the loading logic
    public List<T> load() throws IOException {
        List<T> dataList = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) return dataList; // Return empty list if file doesn't exist

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(parseLine(line)); // Delegate to subclass
            }
        }
        return dataList;
    }

    // ABSTRACT METHODS: To be implemented by subclasses
    protected abstract String convertToString(T data);
    protected abstract T parseLine(String line);
}
