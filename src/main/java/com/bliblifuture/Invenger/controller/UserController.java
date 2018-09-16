package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.InvengerApplication;
import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.response.UploadProfilePictResponse;
import com.bliblifuture.Invenger.service.FileStorageService;
import com.bliblifuture.Invenger.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


@Controller
public class UserController {

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("user",new User());
        if(UserService.getThisUser() != null){
            return "redirect:/profile";
        }
        return "login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, HttpServletRequest request){
        User user = UserService.getThisUser();
        if(user == null){
            return "redirect:/login";
        }
        user.setPosition(positionRepository.findUserPosition(user.getId()));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        model.addAttribute("user",user);
        MyUtils.log(request.getLocalName());
        MyUtils.log(Integer.toString(request.getLocalPort()));
        return "user_profile";
    }

    /*
     * semua validation yang gagal nanti akan dibuatkan error handling
     * sementara diprint log dulu
     * */
    @PostMapping("/profile")
    public String postProfile(Model model,HttpServletRequest request){

        User user = null;
        if(!"".equals(request.getParameter("new-telp")) ){
            if(PhoneValidator.isValid(request.getParameter("new-telp"))){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                user.setTelp(request.getParameter("telp"));
                userRepository.save(user);
            }
            else{
                MyUtils.log("phone number is not valid");
            }
        }

        if(!"".equals(request.getParameter("old-pwd")) ){
            String newPassword = request.getParameter("new-pwd1");
            if(!newPassword.equals(request.getParameter("new-pwd2")) ){
                MyUtils.log("password doesn't match");
                return "redirect:/profile?result=password_doesnot_match";
            }
            if(!PasswordValdator.isValid(newPassword)){
                MyUtils.log("weak password");
                return "redirect:/profile?result=weak_password";
            }

            if(user == null){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
            if(!MyUtils.matches(request.getParameter("old-pwd"),user.getPassword() )){
                MyUtils.log("wrong password");
                return "redirect:/profile?result=wrong_password";
            }

            user.setPassword(MyUtils.getBcryptHash(newPassword));
            userRepository.save(user);
        }
        if(user == null){
            model.addAttribute("user",UserService.getThisUser());
        }
        else{
            model.addAttribute("user",user);
        }
        return "redirect:/profile?result=success";
    }

    @PostMapping("/profile/upload-pict")
    @ResponseBody
    public UploadProfilePictResponse uploadProfilePict
            (@RequestParam("file") MultipartFile file){

        UploadProfilePictResponse response = new UploadProfilePictResponse();
        if(file == null){
            response.setStatusToSuccess();
            return response;
        }
        String fileName = UUID.randomUUID().toString().replace("-","")+
                "."+ FilenameUtils.getExtension(file.getOriginalFilename());

        if(fileStorageService.storeFile(file,fileName, FileStorageService.PathCategory.PROFILE_PICT) ){
            User user = UserService.getThisUser();
            user.setPictureName(fileName);
            userRepository.save(user);
            response.setNew_pict_src("/profile/pict/"+fileName);
            response.setStatusToSuccess();
        }
        else{
            response.setStatusToFailed();
        }

        return response;
    }



}
