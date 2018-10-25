package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.LendmentService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class LendmentController {

    @Autowired
    LendmentService lendmentService;

    @Autowired
    UserService userService;

    @Autowired
    InventoryService inventoryService;

    @GetMapping("lendment/create")
    public String getAssignItemForm(Model model){
        model.addAttribute("users",userService.getAll());
        return "/lendment/lendment_create";
    }

    @PostMapping("lendment/create")
    @ResponseBody
    public RequestResponse assignItemToUser(@Valid @RequestBody LendmentCreateRequest request){
        return lendmentService.createLendment(request);
    }

}
