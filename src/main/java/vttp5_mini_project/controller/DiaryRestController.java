package vttp5_mini_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vttp5_mini_project.model.DiaryEntry;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.service.DiaryService;

@RestController
@RequestMapping("/diary/raw")
public class DiaryRestController {

    @Autowired
    DiaryService diaryService; 

    // show all diary entries 
    @GetMapping("/all")
    public ResponseEntity<List<DiaryEntry>> getAllDiaryEntries(HttpSession session) { 

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        String username = userLogin.getUsername();

        List<DiaryEntry> diaryEntries = diaryService.getAllDiaryEntriesFromRepo(username);
        
        if (diaryEntries.isEmpty()) { 
            // returns 204 no content
            return ResponseEntity.noContent().build(); 

        } else { 
            // returns list of diaryEntries with 200 OK status
            return ResponseEntity.ok(diaryEntries);

        }

    }
    
}
