package vttp5_mini_project;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception { 
        
        HttpSession session = request.getSession(false);

        // allow access to public pages 
        String uri = request.getRequestURI();
        if (uri.equals("/") || uri.equals("/login") || uri.equals("/create-account") || uri.equals("/existing-user") || uri.equals("/new-user")) {
            return true; 

        }

        // check if "currentUser" is present in session 
        if (session != null && session.getAttribute("currentUser") != null) { 
            return true; 

        }

        // redirect to login page if not logged in 
        response.sendRedirect("/");
        return false; 

    }
    
}
