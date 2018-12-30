package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.user.RoleType;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.EditProfileRequest;
import com.bliblifuture.invenger.request.jsonRequest.UserSearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.UserService;
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

    @Autowired
    AccountService accountService;

    @GetMapping("/user/all")
    public String getUserTablePage(Model model){
        model.addAttribute("users", userService.getAll());
        model.addAttribute("user",accountService.getProfile());
        model.addAttribute("roles", RoleType.values());
        model.addAttribute("positions", userService.getAllPosition());
        model.addAttribute("createUserForm", new UserCreateRequest());
        return "user/user_list";
    }

    @PostMapping("/user/create")
    @ResponseBody
    public UserCreateResponse addNewUser(@Valid @ModelAttribute UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/user/edit")
    @ResponseBody
    public RequestResponse editUser(@Valid @ModelAttribute UserEditRequest request) {
        return userService.updateUser(request);
    }

    @PostMapping("/user/delete/{id}")
    @ResponseBody
    public RequestResponse removeUser(@PathVariable("id") Integer id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/user/detail/{id}")
    public String getUserDetail(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("user", userService.getById(id));
        return "user/user_detail";
    }

    @GetMapping("/user/search")
    @ResponseBody
    public SearchResponse searchUser(@RequestParam("search")String query,
                                     @RequestParam("page")Integer page,
                                     @RequestParam("length")Integer length,
                                     @RequestParam(value = "min_level",required = false) Integer minLevel) {

        UserSearchRequest request = new UserSearchRequest();
        request.setQuery(query);
        request.setPageNum(page);
        request.setLength(length);
        request.setMinLevel(minLevel);

        return userService.getSearchResult(request);
    }

    @GetMapping("/user/positions")
    public String getPositionTable(Model model){
        model.addAttribute("positions", userService.getAllPosition());
        return "user/position_list";
    }

    @PostMapping("/user/positions/create")
    @ResponseBody
    public PositionCreateResponse createPosition(@Valid @RequestBody PositionDTO positionDTO) {
        return userService.createPosition(positionDTO);
    }

    @PostMapping("/user/positions/edit")
    @ResponseBody
    public RequestResponse editPosition(@RequestBody PositionDTO editedPosition) {
        if(editedPosition.getId() == null){
            throw new InvalidRequestException("Id can\'t be null");
        }
        return userService.editPosition(editedPosition);
    }

    @PostMapping("/user/positions/delete/{id}")
    @ResponseBody
    public RequestResponse deletePosition(@PathVariable("id") Integer id) {
        return userService.deletePosition(id);
    }

    @GetMapping("/user/datatables")
    @ResponseBody
    public DataTablesResult<UserDataTableResponse> getPaginatedInventories(
            HttpServletRequest servletRequest){
        DataTablesRequest request = new DataTablesRequest(servletRequest);
        return userService.getDatatablesData(request);
    }

}
