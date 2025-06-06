package com.nexbus.frontendNex.controller;

import com.nexbus.frontendNex.dto.LoginRequest;
import com.nexbus.frontendNex.dto.LoginResponse;
import com.nexbus.frontendNex.exception.ApiException;
import com.nexbus.frontendNex.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        if (session.getAttribute("authToken") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest loginRequest, 
                        Model model, HttpSession session) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            session.setAttribute("authToken", loginResponse.getToken());
            session.setAttribute("adminRole", loginResponse.getRole());
            session.setAttribute("adminName", loginResponse.getName());
            return "redirect:/dashboard";
        } catch (ApiException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }
    }

    @GetMapping("/api/audit")
    public String showAudit(Model model, HttpSession session) {
        return authenticateAndRender("audit", model, session);
    }

    @GetMapping("/api/profile")
    public String showProfile(Model model, HttpSession session) {
        return authenticateAndRender("profile", model, session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    private String authenticateAndRender(String viewName, Model model, HttpSession session) {
        String authToken = (String) session.getAttribute("authToken");
        if (authToken == null) {
            return "redirect:/login";
        }
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminRole", session.getAttribute("adminRole"));
        model.addAttribute("authToken", authToken);
        return viewName;
    }
}