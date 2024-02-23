package com.nowhere.springauthserver.api;

import com.nowhere.springauthserver.api.dto.RegisterForm;
import com.nowhere.springauthserver.service.AuthUserService;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    private final AuthUserService authUserService;

    public RegisterController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterForm registerForm, Model model) {
        try {
            authUserService.createUser(registerForm.email(), registerForm.password(), Set.of("USER"));
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // return error page with error message
            model.addAttribute("errorTitle", "Registration error");
            model.addAttribute("errorMessage", "There was an error during registration please try again or contact contact@nowherelean.com.");
            return "error";
        }
    }

}
