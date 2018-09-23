package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.request.ProfileRequest;
import com.bliblifuture.Invenger.response.AlertResponse;
import com.bliblifuture.Invenger.response.FormFieldResponse;
import com.bliblifuture.Invenger.response.UploadProfilePictResponse;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request){
        if(userService.getThisUser() != null){
            return "redirect:/profile";
        }
        User user = new User();
        String requestStatus = (String) request.getSession().getAttribute("status");

        if(requestStatus != null
                && requestStatus.equals("failed") ){
            AlertResponse alert = new AlertResponse(
                    "Failed",
                    "error",
                    "Wrong username/email or password"
            );
            model.addAttribute("alert",alert);
            user.setUsername(request.getSession().getAttribute("username").toString());
            request.removeAttribute("status");
            request.removeAttribute("username");
        }
        model.addAttribute("user",user);

        return "user/login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model){
        User user = userService.getThisUser();
        user.setPosition(userService.getUserPositionById(user.getId()));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        model.addAttribute("user",user);
        return "user/profile";
    }

    @PostMapping("/profile")
    @ResponseBody
    public Map<String,FormFieldResponse> postProfile(@RequestBody ProfileRequest request){
        return userService.editProfile(request);
    }

    @PostMapping("/profile/upload-pict")
    @ResponseBody
    public UploadProfilePictResponse uploadProfilePict
            (@RequestParam("file") MultipartFile file){
        return userService.changeProfilePict(file);
    }

}
