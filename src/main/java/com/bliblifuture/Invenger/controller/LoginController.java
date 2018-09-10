package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("user",new User());
        return "login";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(Authentication authentication){
        return authentication.getDetails().toString();
    }


    @GetMapping("/profile")
    @ResponseBody
    public Object getProfile(Authentication authentication){

//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = (User) authentication.getPrincipal();
        return user1;
    }

}
