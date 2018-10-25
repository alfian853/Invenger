package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.request.jsonRequest.AssignItemsToUserRequest;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LendmentController {

    @Autowired
    UserService userService;

    @Autowired
    InventoryService inventoryService;

    @GetMapping("lendment/assign-form")
    public String getAssignItemForm(Model model){
        model.addAttribute("users",userService.getAll());
        model.addAttribute("inventories",inventoryService.getAll());
        return "lendment/assign_item_to_user";
    }

    @PostMapping("lendment/assign-to-user")
    @ResponseBody
    public Object assignItemToUser(@RequestBody AssignItemsToUserRequest request){
        System.out.println(request);
        return true;
    }

}
