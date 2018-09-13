package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.InvengerApplication;
import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
public class LoginController {

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    UserRepository userRepository;

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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log("testpwd: "+encoder.matches("root",user.getPassword()));
        log("testpwd2: "+encoder.matches("Root123",user.getPassword()));
        model.addAttribute("user",user);
        return "user_profile";
    }

    private void log(String s){
        InvengerApplication.log.info(s);
    }


    @PostMapping("/profile")
    public String postProfile(HttpServletRequest request){
        log(request.getParameter("new-telp"));
        log(Boolean.toString(request.getParameter("new-telp").equals("")));
        log(Boolean.toString(request.getParameter("new-telp") == null));
        log(request.getParameter("new-telp"));

        User user = null;
        if(!"".equals(request.getParameter("new-telp")) ){
            if(PhoneValidator.isValid(request.getParameter("new-telp"))){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                user.setTelp(request.getParameter("telp"));
                userRepository.save(user);
            }
            else{
                log("phone number isnot valid");
            }
        }

        if(!"".equals(request.getParameter("old-pwd")) ){
            String newPassword = request.getParameter("new-pwd1");
            if(!newPassword.equals(request.getParameter("new-pwd2")) ){
                return "password doesn't match";
            }
            if(!PasswordValdator.isValid(newPassword)){
                return "weak password";
            }

            if(user == null){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
            BCryptPasswordEncoder bcyrpt = new BCryptPasswordEncoder();
            if(!bcyrpt.matches(request.getParameter("old-pwd"),user.getPassword() )){
                return "wrong password";
            }

            user.setPassword(bcyrpt.encode(newPassword));
            userRepository.save(user);
        }

        return "user_profile";
    }


}
