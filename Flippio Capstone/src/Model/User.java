package Model;

public abstract class User {
    private final String idNumber;
    private final String password;
    private final String name;

    public User(String idNumber, String password, String name) {
        this.idNumber = idNumber;
        this.password = password;
        this.name = name;
    }

    // Business Logic: Authentication
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getIdNumber() {
        return idNumber;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
}
