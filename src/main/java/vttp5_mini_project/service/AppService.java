package vttp5_mini_project.service;

import java.io.StringReader;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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



    // SPOTIFY INTEGRATION 

    @Value("${spotify.client.id}")
    private String clientId; 

    @Value("${spotify.client.secret}")
    private String clientSecret; 

    @Value("${spotify.redirect.uri}")
    private String redirectUri; 

    @Value("${spotify.auth.url}")
    private String authUrl; 

    @Value("${spotify.token.url}")
    private String tokenUrl; 

    @Value("${spotify.me.url}")
    private String meUrl; 

    // get spotify authorization url
    public String getAuthorizationUrl() {

        return UriComponentsBuilder.fromHttpUrl(authUrl)
                                .queryParam("client_id", clientId)
                                .queryParam("response_type", "code")
                                .queryParam("redirect_uri", redirectUri)
                                .queryParam("scope", "user-read-recently-played")
                                .build()
                                .toString();

    }

    // authenticate and store token 
    public void authenticateAndStoreToken(String code, UserLogin userLogin) {

        ResponseEntity<String> response = exchangeAuthorizationCode(code);

        JSONObject jsonResponse = new JSONObject(response.getBody());

        String accessToken = jsonResponse.getString("access_token");
        String refreshToken = jsonResponse.getString("refresh_token");
        long expiresIn = jsonResponse.getLong("expires_in");

        // get Spotify username 
        String spotifyUsername = getSpotifyUsername(accessToken);

        // calculate expiration time from now in millis
        long tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000);

        // debugging 
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token = " + refreshToken);
        System.out.println("Expires in: " + expiresIn);
        System.out.println("Spotify username: " + spotifyUsername);
        
        // create new UserLogin POJO 
        UserLogin newUser = new UserLogin(
                                    userLogin.getUsername(),
                                    userLogin.getPassword(), 
                                    spotifyUsername,
                                    accessToken, 
                                    refreshToken, 
                                    tokenExpiry
                                    );

        // convert to JSON string 
        String newUserStringData = newPOJOToJsonString(newUser);

        // store in redis 
        appRepo.saveUserToRedis(newUser.getUsername(), newUserStringData);

    }

    // helper method 
    // new UserLogin POJO --> JSON formatted String 
    public String newPOJOToJsonString(UserLogin userLogin) {

        JsonObjectBuilder builder = Json.createObjectBuilder()
                                        .add("username", userLogin.getUsername())
                                        .add("password", userLogin.getPassword())
                                        .add("spotifyUsername", userLogin.getSpotifyUsername())
                                        .add("accessToken", userLogin.getAccessToken())
                                        .add("refreshToken", userLogin.getRefreshToken())
                                        .add("tokenExpiry", String.valueOf(userLogin.getTokenExpiry()));

        JsonObject jObject = builder.build(); 
        return jObject.toString(); 

    }

    // exchange auth code for access and refresh tokens 
    public ResponseEntity<String> exchangeAuthorizationCode(String code) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders(); 
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>(); 
        requestBody.add("grant_type", "authorization_code"); 
        requestBody.add("code", code);
        requestBody.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl, 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );

            return response; 

        }

        catch (Exception e) { 

            return ResponseEntity.status(400).body(e.getMessage());

        }

    }

    // get Spotify username 
    public String getSpotifyUsername(String accessToken) { 

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders(); 
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(meUrl, HttpMethod.GET, request, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String spotifyUsername = jsonResponse.getString("display_name");

        return spotifyUsername;

    }

    


    // RESTCONTROLLER service method 
    public UserLogin getUserDetails(String username) {

        String userDetails = appRepo.getUserDataRedis(username);
        UserLogin userLogin = newJsonStringToPOJO(userDetails);
        
        return userLogin;

    }
    
    // helper method 
    // JSON formatted string --> UserLogin POJO 
    private UserLogin newJsonStringToPOJO(String userData) {

        StringReader sr = new StringReader(userData);
        JsonReader jr = Json.createReader(sr);
        JsonObject jo = jr.readObject();

        UserLogin userLogin = new UserLogin(
            jo.getString("username"),
            jo.getString("password"),
            jo.getString("spotifyUsername"),
            jo.getString("accessToken"),
            jo.getString("refreshToken"),
            Long.valueOf(jo.getString("tokenExpiry"))
        );

        return userLogin;

    }
    
}
