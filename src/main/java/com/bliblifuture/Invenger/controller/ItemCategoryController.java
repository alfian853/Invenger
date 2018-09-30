package com.bliblifuture.Invenger.controller;


import com.bliblifuture.Invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemCategoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @PostMapping("/items/category/create")
    public RequestResponse createCategory(@RequestBody CategoryCreateRequest request){
        return itemCategoryService.createCategory(request);
    }

    @PostMapping("/items/category/edit")
    public CategoryEditResponse editCategory(@RequestBody CategoryEditRequest request){
        return itemCategoryService.updateCategory(request);
    }

    @PostMapping("/items/category/delete")
    public RequestResponse deleteCategory(@RequestParam("id") Integer id){
        return itemCategoryService.deleteCategory(id);
    }


}
