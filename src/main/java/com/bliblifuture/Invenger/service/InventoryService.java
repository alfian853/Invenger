package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.ModelMapper.inventory.InventoryMapper;
import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.exception.DataNotFoundException;
import com.bliblifuture.Invenger.exception.DuplicateEntryException;
import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.model.inventory.Category;
import com.bliblifuture.Invenger.model.inventory.InventoryDocument;
import com.bliblifuture.Invenger.repository.InventoryDocRepository;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.Invenger.response.jsonResponse.InventoryDocDownloadResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.response.viewDto.InventoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
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
    InventoryDocRepository inventoryDocRepository;

    @Autowired
    MyUtils myUtils;

    private final static String PDF_TEMPLATE = "inventory/pdf_template";

    private final InventoryMapper mapper = Mappers.getMapper(InventoryMapper.class);


    public List<InventoryDTO> getAll(){
        return mapper.toInventoryDtoList(inventoryRepository.findAllFetchCategory());
    }

    public InventoryDTO getById(Integer id) throws DataNotFoundException {
        Inventory inventory = inventoryRepository.findInventoryById(id);
        if(inventory == null){
            throw new DataNotFoundException("Inventory Not Found");
        }

        return mapper.toInventoryDto(inventory);
    }

    public InventoryCreateResponse createInventory(InventoryCreateRequest request) throws DuplicateEntryException {
        InventoryCreateResponse response = new InventoryCreateResponse();

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
        newInventory.setType(request.getType().toString());

        if(fileStorageService.storeFile(
                request.getPhoto_file(),
                imgName,
                FileStorageService.PathCategory.INVENTORY_PICT)
        ) {
            try{
                inventoryRepository.save(newInventory);
            }
            catch (DataIntegrityViolationException e){
                e.printStackTrace();
                if(e.getRootCause().getLocalizedMessage().contains("duplicate")){
                    throw new DuplicateEntryException("Inventory name already exist");
                }

            }
            response.setStatusToSuccess();
        }
        else{
            response.setStatusToFailed();
        }

        if(response.isSuccess()){
            response.setInventory_id(newInventory.getId());
        }

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse updateInventory(InventoryEditRequest request) throws DataNotFoundException {

        Inventory inventory = inventoryRepository.getOne(request.getId());

        if(inventory == null){
            throw new DataNotFoundException("Inventory Not Found");
        }

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
            inventory.setType(request.getType().toString());
        }
        if(request.getCategory_id() != null){
            Category category = categoryRepository.getOne(request.getCategory_id());
            inventory.setCategory(category);
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

    public InventoryDocDownloadResponse downloadItemDetail(Integer id) throws Exception {

        InventoryDocDownloadResponse response = new InventoryDocDownloadResponse();

        Inventory inventory = inventoryRepository.findInventoryById(id);
        if(inventory == null){
            throw new DataNotFoundException("Inventory Not Found");
        }
        InventoryDocument doc = inventory.getDocument();

        if(doc == null || !doc.getInventoryLastUpdate().equals(inventory.getUpdatedAt()) ){

            InventoryDTO inventoryDTO = this.getById(id);

            ObjectMapper oMapper = new ObjectMapper();
            Map templateMap = oMapper.convertValue(inventoryDTO, Map.class);

            String filename = fileStorageService.createPdfFromTemplate(
                    PDF_TEMPLATE,
                    templateMap,
                    FileStorageService.PathCategory.INVENTORY_PDF
            );

            if(filename != null){
                if(doc != null){
                    fileStorageService.deleteFile(
                            doc.getFileName(),
                            FileStorageService.PathCategory.INVENTORY_PDF
                    );
                    doc.setFileName(filename);
                    doc.setInventoryLastUpdate(inventory.getUpdatedAt());
                }
                else{
                    doc = InventoryDocument.builder()
                            .inventory(inventoryRepository.getOne(id))
                            .fileName(filename)
                            .inventoryLastUpdate(inventory.getUpdatedAt())
                            .build();
                }

                inventoryDocRepository.save(doc);

                response.setStatusToSuccess();
                response.setInventoryDocUrl("/inventory/document/"+filename);
            }
            else{
                response.setStatusToFailed();
            }

        }
        else{
            response.setStatusToSuccess();
            response.setInventoryDocUrl("/inventory/document/"+doc.getFileName());
        }

        return response;

    }

}
