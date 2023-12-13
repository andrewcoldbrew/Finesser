package myApp.models;

public class User {
    private String userId;
    private String name;
    private String passwordHash;

    public User(String userId, String name, String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
