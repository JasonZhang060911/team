package use_case.signup;

public class SignupInputData {
    private final String username;
    private final String password;
    private final String location;
    private final String gender;

    public SignupInputData(String username, String password, String location, String gender) {
        this.username = username;
        this.password = password;
        this.location = location;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return gender;
    }
}

