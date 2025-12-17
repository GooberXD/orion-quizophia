package Model;

import java.util.Objects;

public class Subject {
    private final String id;
    private String name;
    private String teacherId;

    public Subject(String id, String name, String teacherId) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String toCsvRow() {
        return String.join(",", escape(id), escape(name), escape(teacherId));
    }

    public static Subject fromCsvRow(String row) {
        String[] parts = row.split(",", -1);
        String id = parts.length > 0 ? unescape(parts[0]) : "";
        String name = parts.length > 1 ? unescape(parts[1]) : "";
        String teacherId = parts.length > 2 ? unescape(parts[2]) : "";
        return new Subject(id, name, teacherId);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",").replace("\\\\", "\\");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
