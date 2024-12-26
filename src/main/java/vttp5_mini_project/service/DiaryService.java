package vttp5_mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp5_mini_project.model.DiaryEntry;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.repo.DiaryRepo;

@Service
public class DiaryService {

    @Autowired
    DiaryRepo diaryRepo; 

    // save diary entry to repo
    public void saveDiaryEntryToRepo(String username, DiaryEntry diaryEntry) { 

        String diaryText = diaryEntry.getDiaryText();

        diaryRepo.saveDiaryEntryToRedis(username, diaryText);

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
    // JSON Formatted String --> DiaryEntry POJO 
    private DiaryEntry JsonStringToPOJO(String diaryData) { 

        StringReader sr = new StringReader(diaryData); 
        JsonReader jr = Json.createReader(sr);
        JsonObject jo = jr.readObject(); 

        DiaryEntry diaryEntry = new DiaryEntry(jo.getString("diaryText"));

        return diaryEntry; 

    }
    
}
