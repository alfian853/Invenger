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
//        Inventory inventory = new Inventory();
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        return response;
    }

}
