package vttp5_mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
        String recentlyPlayedSongImage = diaryService.getRecentlyPlayedSongImage(accessToken);

        // empty DiaryEntry object
        DiaryEntry diaryEntry = new DiaryEntry(); 

        // set recently played song 
        diaryEntry.setRecentlyPlayedSong(recentlyPlayedSong);
        diaryEntry.setRecentlyPlayedImage(recentlyPlayedSongImage);
        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-create";

    }

    // redirect to home if GET /diary/created request
    @GetMapping("/created")
    public String getExistingUserPage() { 

        return "redirect:/";

    }

    @PostMapping("/created")
    public String postCreatedEntry(@Valid @ModelAttribute DiaryEntry diaryEntry, BindingResult bindingResult, HttpSession session, Model model) {

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        String username = userLogin.getUsername();

        model.addAttribute("userLogin", userLogin);
        model.addAttribute("diaryEntry", diaryEntry);

        if (bindingResult.hasErrors()) { 
            return "diary-create";

        }
        // if diary entry already made today 
        else if (diaryService.diaryEntryTodayExists(username)) {

            bindingResult.rejectValue("diaryText", "error.diaryEntry", "Diary entry already made today");
            return "diary-create";

        }

        diaryService.saveDiaryEntryToRepo(userLogin.getUsername(), diaryEntry);

        return "diary-created";

    }

    // get diary entry for day dd-MM-yyyy
    @GetMapping("/date/{date}")
    public String getDiaryEntryDate(@PathVariable String date, HttpSession session, Model model) {

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser"); 
        String username = userLogin.getUsername();

        // get diary entry POJO with date from repo 
        DiaryEntry diaryEntry = diaryService.getDiaryEntryDate(date, username);

        // if diaryEntry is null 
        if (diaryEntry == null) {

            model.addAttribute("date", date);
            return "diary-error"; 

        }

        model.addAttribute("diaryEntry", diaryEntry);

        return "diary-entry";

    }

}

