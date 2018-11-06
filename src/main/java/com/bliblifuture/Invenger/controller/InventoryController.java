package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.Utils.PdfGeneratorUtil;
import com.bliblifuture.Invenger.model.inventory.InventoryType;
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
import java.util.HashMap;
import java.util.Map;


@Controller
public class InventoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    UserService userService;

    @Autowired
    PdfGeneratorUtil pdfGeneratorUtil;

    @RequestMapping("/inventory/print")
    public String printPdf() throws Exception{
        Map<String,String> data = new HashMap<>();
        data.put("myName","Nuzha");
        pdfGeneratorUtil.createPdf("greeting", data);
        return "/greeting";
    }

    @GetMapping("/inventory/all")
    public String getInventoryTable(Model model){
        model.addAttribute("inventories", inventoryService.getAll());
        model.addAttribute("user",userService.getProfile());
        model.addAttribute("categories",itemCategoryService.getAllItemCategory());
        model.addAttribute("itemTypes", InventoryType.getAllType());
        model.addAttribute("createItemForm",new InventoryCreateRequest());

        if(userService.currentUserIsAdmin()){
            return "inventory/inventory_list_admin";
        }
        else{
            return "inventory/inventory_list_basic";
        }
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
        if(userService.currentUserIsAdmin()){
            return "inventory/inventory_detail_admin";
        }
        else{
            return "inventory/inventory_detail_basic";
        }    }

}
