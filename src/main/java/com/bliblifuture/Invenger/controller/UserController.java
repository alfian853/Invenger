package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.response.FormFieldResponse;
import com.bliblifuture.Invenger.response.UploadProfilePictResponse;
import com.bliblifuture.Invenger.service.FileStorageService;
import com.bliblifuture.Invenger.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
public class UserController {

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request){
        if(UserService.getThisUser() != null){
            return "redirect:/profile";
        }
        User user = new User();
        String requestStatus = (String) request.getSession().getAttribute("status");
        MyUtils.log("reqg: "+requestStatus);
        if(requestStatus != null
            && requestStatus.equals("failed") ){
            model.addAttribute("alert","Wrong username/email or password");
            model.addAttribute("alert.status","error");
            model.addAttribute("alert.title","Login Failed");
            user.setUsername(request.getSession().getAttribute("username").toString());
            request.removeAttribute("status");
            request.removeAttribute("username");
        }
        model.addAttribute("user",user);

        return "user/login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model){
        User user = UserService.getThisUser();
        if(user == null){
            return "redirect:/login";
        }
        user.setPosition(positionRepository.findUserPosition(user.getId()));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        model.addAttribute("user",user);
        return "user/profile";
    }

    /*
     * semua validation yang gagal nanti akan dibuatkan error handling
     * sementara diprint log dulu
     * */
    @PostMapping("/profile")
    @ResponseBody
    public Map<String,FormFieldResponse> postProfile(@RequestBody Map<String,String> request){

        if(request.get("new-telp") == null || request.get("old-pwd") == null
                || request.get("new-pwd1") == null || request.get("new-pwd2") == null){
            throw new IllegalArgumentException();
        }

        User user = null;
        Map<String,FormFieldResponse> formResponses = new HashMap<>();
        FormFieldResponse formResponse = null;

        MyUtils.log(request.toString());
        MyUtils.log(request.get("new-telp"));
        String new_telp = request.get("new-telp");
        MyUtils.log(new_telp);

        if(!request.get("new-telp").equals("") ){

            formResponse = new FormFieldResponse("new-telp");

            if(PhoneValidator.isValid(new_telp)){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if(!new_telp.equals(user.getTelp())){
                    user.setTelp(new_telp);
                    userRepository.save(user);
                    formResponse.setStatusToSuccess();
                    formResponse.setMessage("Phone number updated successfuly");
                }
            }
            else{
                formResponse.setStatusToFailed();
                formResponse.setMessage(PhoneValidator.getErrorMessage());
            }
            formResponses.put("new-telp",formResponse);
        }

        while(!request.get("old-pwd").equals("") ){

            String newPassword = request.get("new-pwd1");
            formResponse = new FormFieldResponse();

            if(newPassword.equals(request.get("old-pwd"))){
                break;
            }

            if(request.get("new-pwd1").equals("")){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }
            if(request.get("new-pwd2").equals("")){
                formResponse.setField_name("new-pwd2");
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }

            if(!newPassword.equals(request.get("new-pwd2")) ) {
                formResponse.setField_name("new-pwd2");
                formResponse.setStatusToFailed();
                formResponse.setMessage("new password doesn't match");
                formResponses.put("password",formResponse);
                break;
            }

            if(!PasswordValdator.isValid(newPassword)){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("Weak password");
                formResponses.put("password",formResponse);
                break;
            }

            if(user == null){
                user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
            if(MyUtils.matches(request.get("old-pwd"),user.getPassword() )){
                user.setPassword(MyUtils.getBcryptHash(newPassword));
                userRepository.save(user);
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToSuccess();
                formResponse.setMessage("Change password success");

            }
            else{
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToFailed();
                formResponse.setMessage("wrong password");
            }

            formResponses.put("password",formResponse);

            break;
        }


        return formResponses;
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
