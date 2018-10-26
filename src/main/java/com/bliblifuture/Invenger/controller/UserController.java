package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.Invenger.request.jsonRequest.ProfileRequest;
import com.bliblifuture.Invenger.response.jsonResponse.*;
import com.bliblifuture.Invenger.service.PositionService;
import com.bliblifuture.Invenger.service.RoleService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PositionService positionService;

    @Autowired
    MyUtils myUtils;

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request){
        if(userService.getSessionUser() != null){
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
        model.addAttribute("user",userService.getProfile());
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

    @GetMapping("/user/all")
    public String getUserTablePage(Model model){
        model.addAttribute("users", userService.getAll());
        model.addAttribute("user",userService.getProfile());
        model.addAttribute("roles", roleService.getAllRole());
        model.addAttribute("positions", positionService.getAllPosition());
        model.addAttribute("createUserForm", new UserCreateRequest());
        return "user/user_list";
    }

    @PostMapping("/user/create")
    @ResponseBody
    public UserCreateResponse addNewUser(@Valid @ModelAttribute UserCreateRequest request){
        return userService.createUser(request);
    }
    @PostMapping("/user/edit")
    @ResponseBody
    public RequestResponse editUser(@Valid @ModelAttribute UserEditRequest request){
        try {
            return userService.updateUser(request);
        } catch (Exception e) {
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @PostMapping("/user/delete/{id}")
    @ResponseBody
    public RequestResponse removeUser(@PathVariable("id") Integer id){
        return userService.deleteUser(id);
    }

    @GetMapping("/user/detail/{id}")
    public String getUserDetail(Model model, @PathVariable("id") Integer id){
        model.addAttribute("user", userService.getById(id));
        return "user/user_detail";
    }
}
