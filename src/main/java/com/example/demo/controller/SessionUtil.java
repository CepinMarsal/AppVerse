package com.example.demo.controller;

import com.example.demo.dao.UserDAO;
import com.example.demo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtil {
    private static final UserDAO userDAO = new UserDAO();

    public static User getUser(HttpSession session) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String emailHeader = request.getHeader("X-User-Email");
            if (emailHeader != null && !emailHeader.trim().isEmpty()) {
                // Try retrieving from session cache by email first
                User cachedUser = (User) session.getAttribute("user_" + emailHeader);
                if (cachedUser != null) {
                    return cachedUser;
                }
                // If not in cache, load from database and cache it
                try {
                    User dbUser = userDAO.findByEmail(emailHeader);
                    if (dbUser != null && dbUser.isActive()) {
                        session.setAttribute("user_" + emailHeader, dbUser);
                        return dbUser;
                    }
                } catch (Exception e) {
                    // Ignore DB errors
                }
            }
        }
        // Fallback to default session user
        return (User) session.getAttribute("user");
    }
}
