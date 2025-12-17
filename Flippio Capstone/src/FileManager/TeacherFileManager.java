package FileManager;

import Model.*;

public class TeacherFileManager extends FileManager<Teacher> {

    public TeacherFileManager(String fileName) {
        super(fileName);
    }

    @Override
    protected String convertToString(Teacher teacher) {
        // Format: ID,Password,Name
        return teacher.getIdNumber() + "," + teacher.getPassword() + "," + teacher.getName();
    }

    @Override
    protected Teacher parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return null;

        return new Teacher(parts[0], parts[1], parts[2]);
    }
}
