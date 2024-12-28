package vttp5_mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp5_mini_project.model.UserLogin;
import vttp5_mini_project.service.AppService;

@Controller
public class AppController {

    @Autowired
    AppService appService; 

    @GetMapping("/")
    public String getFirstPage() { 

        return "first";

    }

    @GetMapping("/login")
    public String getLoginPage(Model model) { 

        UserLogin userLogin = new UserLogin(); 
        model.addAttribute("userLogin", userLogin);

        return "login";

    }

    @GetMapping("/create-account")
    public String getNewUserPage(Model model) {

        UserLogin userLogin = new UserLogin(); 
        model.addAttribute("userLogin", userLogin); 

        return "create-account";

    }

    @PostMapping("/existing-user")
    public String postExistingUserPage(@Valid @ModelAttribute UserLogin userLogin, BindingResult bindingResult, Model model, HttpSession session) {

        if (bindingResult.hasErrors()) {

            return "login";

        } else if (!bindingResult.hasErrors() && !appService.checkUsernameExistsInRepo(userLogin)) {

            // check if username is in redis 
            bindingResult.rejectValue("username", "error.userLogin", "Username doesn't exist");
            return "login";

        } else if (!bindingResult.hasErrors() && appService.checkUsernameExistsInRepo(userLogin) && !appService.checkUsernamePassword(userLogin)) {

            // check if username + password is correct 
            bindingResult.rejectValue("password", "error.userLogin", "Password is incorrect");
            return "login";

        }

        model.addAttribute("userLogin", userLogin);

        session.setAttribute("currentUser", userLogin);

        return "existing-user";

    }

    @PostMapping("/new-user")
    public String postNewUserPage(@Valid @ModelAttribute UserLogin userLogin, BindingResult bindingResult, Model model, HttpSession session) {

        if (bindingResult.hasErrors()) {

            return "create-account";

        } else if (!bindingResult.hasErrors() && appService.checkUsernameExistsInRepo(userLogin)) {

            // check if username is in redis (already taken)
            bindingResult.rejectValue("username", "error.userLogin", "Username is already taken");
            return "create-account";

        } 

        model.addAttribute("userLogin", userLogin);

        // add username + password to redis
        appService.saveUserToRepo(userLogin);

        session.setAttribute("currentUser", userLogin);

        return "new-user";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { 

        // invalidate session to log user out 
        session.invalidate();

        return "logout";

    }


    // SPOTIFY INTEGRATION

    @GetMapping("/connect-spotify")
    public String connectToSpotify(HttpSession session) {

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");

        if (userLogin == null) { 

            // TODO
            return "not-logged-in";
        } 
        
        else { 

            String authorizationUrl = appService.getAuthorizationUrl();

            try { 
                return "redirect:" + authorizationUrl;

            } 
            catch (Exception e) {
                return e.getMessage();

            }

        }

        
    }

    @GetMapping("/callback")
    public String getCallbackPage(@RequestParam(required = false) String code, 
                                    @RequestParam(required = false) String error, 
                                    Model model, HttpSession session) { 

        UserLogin userLogin = (UserLogin) session.getAttribute("currentUser");
        model.addAttribute("userLogin", userLogin);

        if (error != null) {

            model.addAttribute("error", error);
            
            // redirect to spotify login 
            return "callback-failure";

        } else {

            try { 

                // authenticate using code 
                appService.authenticateAndStoreToken(code, userLogin);
                model.addAttribute("success", userLogin);
    
            }
    
            catch (Exception e) { 
    
                model.addAttribute("error", e.getMessage());
    
            }

        }

        return "callback";

    }

    
}
