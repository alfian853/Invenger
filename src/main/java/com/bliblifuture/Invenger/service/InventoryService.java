package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.Inventory;
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
        Inventory newInventory = new Inventory();
        newInventory.setName(request.getName());
        newInventory.setQuantity(request.getQuantity());
        newInventory.setPrice(request.getPrice());
        newInventory.setDescription(request.getDescription());
        inventoryRepository.save(newInventory);
        response.setStatusToSuccess();
        return response;
    }

}
