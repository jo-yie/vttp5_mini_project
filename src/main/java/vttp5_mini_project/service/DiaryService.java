package vttp5_mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
                                        .add("recentlyPlayedSong", diaryEntry.getRecentlyPlayedSong()
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
            jo.getString("recentlyPlayedSong"));

        return diaryEntry; 

    }


    // SPOTIFY IMPLEMENTATION

    @Autowired
    AppRepo appRepo;

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

        if (jsonItemsArray.isEmpty()) { 

            // no songs found 
            // TODO

        }

        // get most recent track 
        JSONObject firstItem = jsonItemsArray.getJSONObject(0);
        JSONObject track = firstItem.getJSONObject("track");
        String songName = track.getString("name"); 

        return songName;

    }

    // get recently played song cover art
    public String[] getRecentlyPlayedSongImage(String accessToken) { 

        RestTemplate restTemplate = new RestTemplate(); 

        HttpHeaders headers = new HttpHeaders(); 
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(recentlyPlayedUrl, HttpMethod.GET, request, String.class);

        // get "items" array
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray jsonItemsArray = jsonResponse.getJSONArray("items");

        if (jsonItemsArray.isEmpty()) { 

            // no songs found 
            // TODO

        }

        // get most recent track 
        JSONObject firstItem = jsonItemsArray.getJSONObject(0);
        JSONObject track = firstItem.getJSONObject("track");
        JSONArray images = track.getJSONArray("images");

        String[] imageArray = new String[3]; 
        for (int i = 0; i < 3; i++) { 
            imageArray[i] = images.getString(i);
        }

        return imageArray;

    }

    // get access token 
    public String getAccessToken(String username) {

        String userData = appRepo.getUserDataRedis(username);

        UserLogin userLogin = jsonStringToPOJO(userData);

        String accessToken = userLogin.getAccessToken();

        return accessToken;

    }

    // helper method 
    // JSON Formatted String --> UserLogin POJO 
    private UserLogin jsonStringToPOJO(String jsonData) { 

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

    
}
