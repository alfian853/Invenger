package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.Inventory;
import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {


    @Autowired
    InventoryRepository inventoryRepository;


    public List<Inventory> getAll(){
        return inventoryRepository.findAll();
    }

    public Inventory getById(Integer id){
        return inventoryRepository.findInventoryById(id);
    }

    // not implemented yet
    public RequestResponse createInventory(InventoryCreateRequest request){
        RequestResponse response = new RequestResponse();
        Category newCategory = new Category();
        newCategory.setId(request.getCategory_id());
        System.out.println(request);
        Inventory newInventory = new Inventory();
        newInventory.setName(request.getName());
        newInventory.setQuantity(request.getQuantity());
        newInventory.setPrice(request.getPrice());
        newInventory.setDescription(request.getDescription());
        newInventory.setCategory(newCategory);
//        inventoryRepository.save(newCategory);
//        newInventory.setCategory(request.);
//        newInventory.setImage(request.getPhoto_file());
        inventoryRepository.save(newInventory);
        response.setStatusToSuccess();
        return response;
    }

}
