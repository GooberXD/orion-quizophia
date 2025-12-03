package FileManager;

import Model.*;

public class StudentFileManager extends FileManager<Student> {

    public StudentFileManager(String fileName) {
        super(fileName);
    }

    @Override
    protected String convertToString(Student student) {
        // Format: ID,Password,Name
        return student.getIdNumber() + "," + student.getPassword() + "," + student.getName();
    }

    @Override
    protected Student parseLine(String line) {
        String[] parts = line.split(",");
        // Validation to prevent crashes on empty lines
        if (parts.length < 3) return null;

        return new Student(parts[0], parts[1], parts[2]);
    }
}
