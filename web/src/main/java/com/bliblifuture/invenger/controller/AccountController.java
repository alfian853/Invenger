package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.response.jsonResponse.AlertResponse;
import com.bliblifuture.invenger.response.jsonResponse.FormFieldResponse;
import com.bliblifuture.invenger.response.jsonResponse.UploadProfilePictResponse;
import com.bliblifuture.invenger.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    MyUtils myUtils;

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request){
        if(accountService.getSessionUser() != null){
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
            // persistent login form value
            user.setUsername(request.getSession().getAttribute("username").toString());
            request.removeAttribute("status");
            request.removeAttribute("username");
        }
        model.addAttribute("user",user);

        return "user/login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model){
        model.addAttribute("user", accountService.getProfile());
        return "user/profile";
    }

    @PostMapping("/profile")
    @ResponseBody
    public Map<String,FormFieldResponse> postProfile(@RequestBody EditProfileRequest request) {
        return accountService.editProfile(request);
    }

    @PostMapping("/profile/upload-pict")
    @ResponseBody
    public UploadProfilePictResponse uploadProfilePict
            (@RequestParam("file") MultipartFile file){
        return accountService.changeProfilePict(file);
    }


}
