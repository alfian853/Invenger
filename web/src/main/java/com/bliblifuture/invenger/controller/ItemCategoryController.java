package com.bliblifuture.invenger.controller;


import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ItemCategoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @PostMapping("/category/create")
    @ResponseBody
    public RequestResponse createCategory(@Valid @RequestBody CategoryCreateRequest request){
        return itemCategoryService.createCategory(request);
    }

    @PostMapping("/category/edit")
    @ResponseBody
    public CategoryEditResponse editCategory(@Valid @RequestBody CategoryEditRequest request){
        try {
            return itemCategoryService.updateCategory(request);
        } catch (Exception e) {
            CategoryEditResponse response = new CategoryEditResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @PostMapping("/category/delete/{id}")
    @ResponseBody
    public RequestResponse deleteCategory(@PathVariable("id") Integer id) {
        return itemCategoryService.deleteCategory(id);
    }

    @GetMapping("/category/all")
    public String getCategoryList(Model model){
        model.addAttribute("categories", itemCategoryService.getAllItemCategory(true));
        return "inventory/inventory_category_list";
    }

    @GetMapping("/category/all2")
    @ResponseBody
    public Object getCategoryList2(Model model){
//        entity.addAttribute("categories", );
        return itemCategoryService.getAllItemCategory(true);
    }



}
