package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LoginController {

    @GetMapping("login")
    public String getTest(Model model){
        model.addAttribute("user",new User());
        return "login";
    }
}
