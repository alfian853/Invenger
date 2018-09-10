package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.InvengerApplication;
import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import javafx.geometry.Pos;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Controller
public class LoginController {

    @Autowired
    PositionRepository positionRepository;

    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("user",new User());
//        if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
//            return "redirect:/profile";
//        }
        return "login";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(Authentication authentication){
        return authentication.getDetails().toString();
    }


    @GetMapping("/profile")
    public String getProfile(Model model){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPosition(positionRepository.findUserPosition(user.getId()));
        model.addAttribute("user",user);
        return "user_profile";
    }

}
