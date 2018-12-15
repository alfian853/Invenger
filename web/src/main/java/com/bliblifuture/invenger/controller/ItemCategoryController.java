package com.bliblifuture.invenger.controller;


import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/category")
public class ItemCategoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @PostMapping("/create")
    @ResponseBody
    public RequestResponse createCategory(@Valid @RequestBody CategoryCreateRequest request){
        return itemCategoryService.createCategory(request);
    }

    @PostMapping("/edit")
    @ResponseBody
    public RequestResponse editCategory(@Valid @RequestBody CategoryEditRequest request){
        return itemCategoryService.updateCategory(request);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public RequestResponse deleteCategory(@PathVariable("id") Integer id) {
        return itemCategoryService.deleteCategory(id);
    }

    @GetMapping("/all")
    public String getCategoryList(Model model){
        model.addAttribute("categories", itemCategoryService.getAllItemCategory(true));
        return "inventory/inventory_category_list";
    }



}
