package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.repository.*;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.InventoryService;
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


    @GetMapping("/modal")
    public String getModal(Model model, HttpServletRequest request){
        model.addAttribute("categories",itemCategoryService.getAllItemCategory());
        model.addAttribute("createItemForm",new InventoryCreateRequest());
        return "modal";
    }

    @PostMapping("/inventory/create")
    @ResponseBody
    public RequestResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request){
        return inventoryService.createInventory(request);
    }


}
