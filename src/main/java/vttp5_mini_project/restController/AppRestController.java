package vttp5_mini_project.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.service.AppService;

@RestController
@RequestMapping("/user/raw")
public class AppRestController {

    @Autowired
    AppService appService; 

    @GetMapping("/details")
    public ResponseEntity<UserLogin> getUserDetails(HttpSession session) { 

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        String username = userLogin.getUsername(); 

        UserLogin userDetails = appService.getUserDetails(username);

        if (userDetails == null) { 
            // returns 204 no content
            return ResponseEntity.noContent().build(); 

        } else { 
            // returns list of diaryEntries with 200 OK status
            return ResponseEntity.ok(userDetails);

        }

    }
    
}
