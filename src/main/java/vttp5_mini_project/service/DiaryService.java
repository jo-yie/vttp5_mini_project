package vttp5_mini_project.service;

import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp5_mini_project.model.DiaryEntry;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.repo.AppRepo;
import vttp5_mini_project.repo.DiaryRepo;

@Service
public class DiaryService {

    @Autowired
    DiaryRepo diaryRepo; 

    // save diary entry to repo
    public void saveDiaryEntryToRepo(String username, DiaryEntry diaryEntry) { 

        // get diaryEntry id 
        String id = diaryEntry.getId();

        // convert diaryEntry POJO --> JSON Formatted String
        String jDiaryEntry = POJOToJsonString(diaryEntry); 

        diaryRepo.saveDiaryEntryToRedis(username, id, jDiaryEntry);

    }

    // get all diary entries from repo 
    public List<DiaryEntry> getAllDiaryEntriesFromRepo(String username) { 

        List<Object> diaryEntriesRedis = diaryRepo.getAllDiaryEntries(username);

        // create a list to hold DiaryEntry POJOs 
        List<DiaryEntry> diaryEntries = new ArrayList<>(); 

        // convert each JSON String to DiaryEntry POJO 
        for (Object joDiaryEntry : diaryEntriesRedis) { 

            String jsDiaryEntry = joDiaryEntry.toString(); 
            DiaryEntry diaryEntry = JsonStringToPOJO(jsDiaryEntry);
            diaryEntries.add(diaryEntry);

        }

        return diaryEntries; 

    }

    // helper method 
    // DiaryEntry POJO --> JSON Formatted String 
    private String POJOToJsonString(DiaryEntry diaryEntry) { 

        JsonObjectBuilder builder = Json.createObjectBuilder()
                                        .add("id", diaryEntry.getId())
                                        .add("date", String.valueOf(diaryEntry.getDate()))
                                        .add("diaryText", diaryEntry.getDiaryText())
                                        .add("recentlyPlayedSong", diaryEntry.getRecentlyPlayedSong())
                                        .add("recentlyPlayedImage", diaryEntry.getRecentlyPlayedImage()
                                        );

        JsonObject jObject = builder.build(); 
        return jObject.toString(); 

    }

    // helper method 
    // JSON Formatted String --> DiaryEntry POJO 
    private DiaryEntry JsonStringToPOJO(String diaryData) { 

        StringReader sr = new StringReader(diaryData); 
        JsonReader jr = Json.createReader(sr);
        JsonObject jo = jr.readObject(); 

        DiaryEntry diaryEntry = new DiaryEntry(
            jo.getString("id"),
            Long.parseLong(jo.getString("date")),
            jo.getString("diaryText"), 
            jo.getString("recentlyPlayedSong"),
            jo.getString("recentlyPlayedImage"));

        return diaryEntry; 

    }



    // SPOTIFY IMPLEMENTATION

    @Autowired
    AppRepo appRepo;

    @Value("${spotify.client.id}")
    private String clientId; 

    @Value("${spotify.client.secret}")
    private String clientSecret; 

    @Value("${spotify.token.url}")
    private String tokenUrl; 

    @Value("${spotify.recently.played.url}")
    String recentlyPlayedUrl;

    // get recently played song 
    public String getRecentlyPlayedSong(String accessToken) { 

        RestTemplate restTemplate = new RestTemplate(); 

        HttpHeaders headers = new HttpHeaders(); 
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(recentlyPlayedUrl, HttpMethod.GET, request, String.class);

        // get "items" array
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray jsonItemsArray = jsonResponse.getJSONArray("items");

        // no songs found
        if (jsonItemsArray.isEmpty()) { 

            return "No recently played song";

        }

        // get most recent track 
        JSONObject firstItem = jsonItemsArray.getJSONObject(0);
        JSONObject track = firstItem.getJSONObject("track");
        String songName = track.getString("name"); 

        return songName;

    }

    // get recently played song cover art
    public String getRecentlyPlayedSongImage(String accessToken) { 

        RestTemplate restTemplate = new RestTemplate(); 

        HttpHeaders headers = new HttpHeaders(); 
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(recentlyPlayedUrl, HttpMethod.GET, request, String.class);

        // get "items" array
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray jsonItemsArray = jsonResponse.getJSONArray("items");

        // no songs found
        if (jsonItemsArray.isEmpty()) { 

            return "https://onlinepngtools.com/images/examples-onlinepngtools/64-megapixels.png";

        }

        // get most recent track 
        JSONObject firstItem = jsonItemsArray.getJSONObject(0);
        JSONObject track = firstItem.getJSONObject("track");
        JSONObject album = track.getJSONObject("album");
        JSONArray images = album.getJSONArray("images");

        String image = images.getJSONObject(0).getString("url");

        return image;

    }

    // helper method 
    // check if token in redis is expired 
    private boolean isTokenExpired(long expiryTime) {
        return System.currentTimeMillis() > expiryTime;

    }

    // refresh access token 
    public String refreshAccessToken(String refreshToken) {

        RestTemplate restTemplate = new RestTemplate(); 

        HttpHeaders headers = new HttpHeaders(); 
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.getString("access_token");

    }


    // get access token from redis 
    public String getAccessToken(String username) {

        String userData = appRepo.getUserDataRedis(username);

        UserLogin userLogin = newJsonStringToPOJO(userData);

        String accessToken = userLogin.getAccessToken();

        // check if access token is expired 
        long expiry = userLogin.getTokenExpiry(); 

        if (isTokenExpired(expiry)) {

            // get new token 
            String refreshToken = userLogin.getRefreshToken();
            accessToken = refreshAccessToken(refreshToken);

        }

        return accessToken;

    }

    // helper method 
    // JSON Formatted String --> UserLogin POJO 
    private UserLogin newJsonStringToPOJO(String jsonData) { 

        StringReader sr = new StringReader(jsonData); 
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

    // get diary entry with date dd-MM-yyyy
    public DiaryEntry getDiaryEntryDate(String dateRequest, String username) {

        List<DiaryEntry> diaryEntries = getAllDiaryEntriesFromRepo(username); 
        
        for (DiaryEntry entry: diaryEntries) { 

            // convert entry.getDate() to String 
            String dateFromRepo = convertMillisToDateString(entry.getDate());

            if (dateRequest.equals(dateFromRepo)) { 

                return entry;

            }

        }

        return null;

    }

    // check if diary entry for today's date exists 
    public boolean diaryEntryTodayExists(String username) {

        // today's date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate today = LocalDate.now();
        String todayString = today.format(dtf); 

        if (getDiaryEntryDate(todayString, username) != null) { 
            return true;

        }

        return false;

    }

    // helper method 
    // convert long millis --> String date 
    private String convertMillisToDateString(long millis) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(dtf);

    }

}
