package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.repository.*;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.UserService;
import com.bliblifuture.Invenger.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
public class InventoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    UserService userService;


    @GetMapping("/modal")
    public String getModal(Model model, HttpServletRequest request){
        model.addAttribute("categories",itemCategoryService.getAllItemCategory());
        model.addAttribute("createItemForm",new InventoryCreateRequest());
        return "modal";
    }
    @GetMapping("/table")
    public String getTablePage(Model model){
        model.addAttribute("inventories", inventoryService.getAll());
        model.addAttribute("user",userService.getProfile());
        return "table";
    }

    @GetMapping("/table2")
    public String getTablePage2(Model model){
        model.addAttribute("inventories", inventoryService.getAll());
        model.addAttribute("categories",itemCategoryService.getAllItemCategory());
        model.addAttribute("createItemForm",new InventoryCreateRequest());
        return "table2";
    }

    @PostMapping("/inventory/create")
    @ResponseBody
    public RequestResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request){
        return inventoryService.createInventory(request);
    }

    @PostMapping("/inventory/edit")
    public RequestResponse editInventory(@RequestBody InventoryEditRequest request){
        try {
            return inventoryService.updateInventory(request);
        } catch (Exception e) {
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @PostMapping("/inventory/delete")
    @ResponseBody
    public RequestResponse removeInventory(@RequestParam("id") Integer id){
        return inventoryService.deleteInventory(id);
    }

}
