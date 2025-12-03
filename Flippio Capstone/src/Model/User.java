package Model;

public abstract class User {
    // Encapsulated Fields (Private)
    private String idNumber;
    private String password;
    private String name;

    // Constructor
    public User(String idNumber, String password, String name) {
        this.idNumber = idNumber;
        this.password = password;
        this.name = name;
    }

    // Business Logic: Authentication
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Getters
    public String getIdNumber() {
        return idNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    // Setters (if needed for editing profiles later)
    public void setName(String name) {
        this.name = name;
    }
}
