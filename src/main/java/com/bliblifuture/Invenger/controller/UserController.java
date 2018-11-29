package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.exception.DefaultException;
import com.bliblifuture.Invenger.model.user.RoleType;
import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.Invenger.request.jsonRequest.ProfileRequest;
import com.bliblifuture.Invenger.response.jsonResponse.*;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.Invenger.response.viewDto.PositionDTO;
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
    public Map<String,FormFieldResponse> postProfile(@RequestBody ProfileRequest request) throws Exception {
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
        model.addAttribute("roles", RoleType.values());
        model.addAttribute("positions", userService.getAllPosition());
        model.addAttribute("createUserForm", new UserCreateRequest());
        return "user/user_list";
    }

    @PostMapping("/user/create")
    @ResponseBody
    public UserCreateResponse addNewUser(@Valid @ModelAttribute UserCreateRequest request) throws Exception {
        return userService.createUser(request);
    }

    @PostMapping("/user/edit")
    @ResponseBody
    public RequestResponse editUser(@Valid @ModelAttribute UserEditRequest request) throws Exception {
        return userService.updateUser(request);
    }

    @PostMapping("/user/delete/{id}")
    @ResponseBody
    public RequestResponse removeUser(@PathVariable("id") Integer id) throws Exception {
        return userService.deleteUser(id);
    }

    @GetMapping("/user/detail/{id}")
    public String getUserDetail(Model model, @PathVariable("id") Integer id) throws Exception {
        model.addAttribute("user", userService.getById(id));
        return "user/user_detail";
    }

    @GetMapping("/user/search")
    @ResponseBody
    public SearchResponse searchUser(@RequestParam("search")String query,
                                     @RequestParam("page")Integer page,
                                     @RequestParam("length")Integer length) {

        return userService.getSearchedUser(query,page,length);
    }

    @GetMapping("/user/positions")
    public String getPositionTable(Model model){
        model.addAttribute("positions", userService.getAllPosition());
        return "user/position_list";
    }

    @PostMapping("/user/positions/create")
    @ResponseBody
    public PositionCreateResponse createPosition(@Valid @RequestBody PositionDTO positionDTO) throws DefaultException {
        return userService.createPosition(positionDTO);
    }

    @PostMapping("/user/positions/edit")
    @ResponseBody
    public RequestResponse editPosition(@RequestBody PositionDTO editedPosition) throws DefaultException {
        return userService.editPosition(editedPosition);
    }

    @PostMapping("/user/positions/delete/{id}")
    @ResponseBody
    public RequestResponse deletePosition(@PathVariable("id") Integer id) throws DefaultException {
        return userService.deletePosition(id);
    }

}
