package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.User;
import netscape.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping("login")
    public String getLogin(Model model, Principal principal){

        model.addAttribute("user",new User());
        return "login";
    }


}
