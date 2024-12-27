package vttp5_mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vttp5_mini_project.model.DiaryEntry;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.service.DiaryService;

@Controller
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    DiaryService diaryService;

    // shows all diary entries 
    @GetMapping("/all")
    public String getAllEntries(HttpSession session, Model model) { 

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        model.addAttribute("userLogin", userLogin);

        model.addAttribute("diaryEntries", diaryService.getAllDiaryEntriesFromRepo(userLogin.getUsername()));

        return "diary-all";
        
    }

    // create new diary entry
    @GetMapping("/create")
    public String getCreateEntry(HttpSession session, Model model) {

        // UserLogin object to get username
        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        String username = userLogin.getUsername(); 

        // get recently played song 
        String accessToken = diaryService.getAccessToken(username);
        String recentlyPlayedSong = diaryService.getRecentlyPlayedSong(accessToken);
        // model.addAttribute("recentlyPlayedSong", recentlyPlayedSong);

        System.out.println("testing in get mapping /create");
        System.out.println(recentlyPlayedSong);

        // empty DiaryEntry object
        DiaryEntry diaryEntry = new DiaryEntry(); 

        // set recently played song 
        diaryEntry.setRecentlyPlayedSong(recentlyPlayedSong);
        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-create";

    }

    @PostMapping("/created")
    public String postDiaryEntry(@ModelAttribute DiaryEntry diaryEntry, HttpSession session, Model model) {

        System.out.println("testing");
        System.out.println(diaryEntry.getId());
        System.out.println(diaryEntry.getDate());
        System.out.println(diaryEntry.getDiaryText());
        System.out.println(diaryEntry.getRecentlyPlayedSong());

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");

        diaryService.saveDiaryEntryToRepo(userLogin.getUsername(), diaryEntry);

        model.addAttribute("userLogin", userLogin);

        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-created";

    }
    
}

