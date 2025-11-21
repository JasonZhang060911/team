package entity;


import java.util.Map;

public class User {
    private final String name;
    private final String password;
    private final String location;
    private final String gender;
    private Map<String, Boolean> style;

    public User(String name, String password, String location, String gender) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.name = name;
        this.password = password;
        this.location = location;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return  gender;
    }

    public Map<String, Boolean> getStyle() {
        return style;
    }

    public void setStyle(Map<String, Boolean> style) {
        this.style = style;
    }
}
