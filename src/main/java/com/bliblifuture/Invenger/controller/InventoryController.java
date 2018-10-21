package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.InventoryType;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.ItemCategoryService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class InventoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    UserService userService;

    @GetMapping("/inventory/all")
    public String getTablePage(Model model){
        model.addAttribute("inventories", inventoryService.getAll());
        model.addAttribute("user",userService.getProfile());
        model.addAttribute("categories",itemCategoryService.getAllItemCategory());
        model.addAttribute("itemTypes", InventoryType.getAllType());
        model.addAttribute("createItemForm",new InventoryCreateRequest());
        return "inventory/inventory_list";
    }

    @PostMapping("/inventory/create")
    @ResponseBody
    public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request){
        return inventoryService.createInventory(request);
    }

    @PostMapping("/inventory/edit")
    @ResponseBody
    public RequestResponse editInventory(@Valid @ModelAttribute InventoryEditRequest request){
        try {
            return inventoryService.updateInventory(request);
        } catch (Exception e) {
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @PostMapping("/inventory/delete/{id}")
    @ResponseBody
    public RequestResponse removeInventory(@PathVariable("id") Integer id){
        return inventoryService.deleteInventory(id);
    }

    @GetMapping("/inventory/detail/{id}")
    public String getInventoryDetail(Model model, @PathVariable("id") Integer id){
        model.addAttribute("inventory", inventoryService.getById(id));
        model.addAttribute("itemTypes", InventoryType.getAllType());
        return "inventory/inventory_detail";
    }



}
