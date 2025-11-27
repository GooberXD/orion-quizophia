public abstract class User {
    private String id;
    private String name;
    private String role; // edited

    public User(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role; // edited
    }

    public abstract String action();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; } // edited
    public void setRole(String role) {

        if(this.role.equalsIgnoreCase("Student")) {
            this.role = "Student";
        }else if(this.role.equalsIgnoreCase("Teacher")) {
            this.role = "Teacher";
        }

    } // edited

    // public boolean authenticate(){ return false; }; // to edit
}
