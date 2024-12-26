package vttp5_mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String getCreateEntry(Model model) {

        DiaryEntry diaryEntry = new DiaryEntry(); 
        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-create";

    }

    @PostMapping("/created")
    public String postDiaryEntry(@ModelAttribute DiaryEntry diaryEntry, HttpSession session, Model model) {

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");

        diaryService.saveDiaryEntryToRepo(userLogin.getUsername(), diaryEntry);

        model.addAttribute("userLogin", userLogin);

        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-created";

    }
    
}

