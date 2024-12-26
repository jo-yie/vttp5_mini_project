package vttp5_mini_project.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.repo.AppRepo;

@Service
public class AppService {

    @Autowired
    AppRepo appRepo; 

    // check if username (key) is in repo
    public Boolean checkUsernameExistsInRepo(UserLogin userLogin) {

       return appRepo.checkUsernameExistsInRedis(userLogin.getUsername());

    }

    // check if username + password is correct 
    public Boolean checkUsernamePassword(UserLogin userLogin) {

        String userData = appRepo.getUserDataRedis(userLogin.getUsername());
        UserLogin userRedis = JsonStringToPOJO(userData);

        if (userLogin.getPassword().equals(userRedis.getPassword())) {
            return true;
        }

        else {
            return false;
        }

    }

    // create user 
    // insert username + password into redis 
    public void saveUserToRepo(UserLogin userLogin) {

        String username = userLogin.getUsername();
        String userData = POJOToJsonString(userLogin);

        appRepo.saveUserToRedis(username, userData);

    }

    // helper method 
    // UserLogin POJO --> JSON formatted string 
    private String POJOToJsonString(UserLogin userLogin) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                                        .add("username", userLogin.getUsername())
                                        .add("password", userLogin.getPassword());

        JsonObject jObject = builder.build(); 
        return jObject.toString(); 

    }

    // helper method 
    // JSON formatted string --> UserLogin POJO 
    private UserLogin JsonStringToPOJO(String userData) {

        StringReader sr = new StringReader(userData);
        JsonReader jr = Json.createReader(sr);
        JsonObject jo = jr.readObject();

        UserLogin userLogin = new UserLogin(
            jo.getString("username"),
            jo.getString("password")
        );

        return userLogin;

    }


    
}
