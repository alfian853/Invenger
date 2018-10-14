package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.model.Inventory;
import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {



    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MyUtils myUtils;


    public List<Inventory> getAll(){
        return inventoryRepository.findAll();
    }

    public Inventory getById(Integer id){
        return inventoryRepository.findInventoryById(id);
    }

    public RequestResponse createInventory(InventoryCreateRequest request){
        RequestResponse response = new RequestResponse();

        String imgName = UUID.randomUUID().toString().replace("-","")+
                "."+ FilenameUtils.getExtension(request.getPhoto_file().getOriginalFilename());

        Category newCategory = new Category();
        newCategory.setId(request.getCategory_id());

        Inventory newInventory = new Inventory();
        newInventory.setName(request.getName());
        newInventory.setQuantity(request.getQuantity());
        newInventory.setPrice(request.getPrice());
        newInventory.setImage(imgName);
        newInventory.setDescription(request.getDescription());
        newInventory.setCategory(newCategory);

        if(fileStorageService.storeFile(
                request.getPhoto_file(),
                imgName,
                FileStorageService.PathCategory.INVENTORY_PICT)
        ) {
            inventoryRepository.save(newInventory);
            response.setStatusToSuccess();
        }
        else{
            response.setStatusToFailed();
        }

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse updateInventory(InventoryEditRequest request){

        Inventory inventory = inventoryRepository.getOne(request.getId());
        System.out.println(request);
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        if(request.getName() != null){
            inventory.setName(request.getName());
        }
        if(request.getPrice() != null){
            inventory.setPrice(request.getPrice());
        }
        if(request.getQuantity() != null){
            inventory.setQuantity(request.getQuantity());
        }
        if(request.getDescription() != null){
            inventory.setDescription(request.getDescription());
        }
        if(request.getType() != null){
            inventory.setType(request.getType());
        }
        if(request.getCategory_id() != null){
            Category category = categoryRepository.getOne(request.getCategory_id());
            inventory.setCategory(category);
            System.out.println("heheee");
        }
        if(request.getPict() != null){

            String newFileName = myUtils.getRandomFileName(request.getPict());

            if(fileStorageService.storeFile(request.getPict(),newFileName,
                    FileStorageService.PathCategory.INVENTORY_PICT) ){

                String oldFileName = inventory.getImage();
                fileStorageService.deleteFile(oldFileName, FileStorageService.PathCategory.INVENTORY_PICT);
                inventory.setImage(newFileName);
            }
            else {
                response.setStatusToFailed();
                response.setMessage("Internal server error");
            }

        }

        inventoryRepository.save(inventory);

        return response;
    }

    public RequestResponse deleteInventory(Integer id){
        RequestResponse response = new RequestResponse();
        inventoryRepository.deleteById(id);
        response.setStatusToSuccess();
        return response;
    }

}
