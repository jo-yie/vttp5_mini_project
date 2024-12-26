package vttp5_mini_project.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserLogin {

    @NotEmpty(message = "Username is mandatory")
    @Size(min = 5, max = 20, message = "Username must be between 5 to 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only alphanumeric characters and no spaces")
    private String username; 

    @NotEmpty(message = "Password is mandatory")
    @Size(min = 5, max = 20, message = "Password must be between 5 to 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Password must contain only alphanumeric characters and no spaces")
    private String password;
    
    public UserLogin() {
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } 
    
}
