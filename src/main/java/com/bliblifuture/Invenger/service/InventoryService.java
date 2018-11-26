package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.ModelMapper.FieldMapper;
import com.bliblifuture.Invenger.ModelMapper.inventory.InventoryMapper;
import com.bliblifuture.Invenger.Utils.DataTablesUtils;
import com.bliblifuture.Invenger.Utils.MyUtils;
import com.bliblifuture.Invenger.Utils.QuerySpec;
import com.bliblifuture.Invenger.exception.DataNotFoundException;
import com.bliblifuture.Invenger.exception.DuplicateEntryException;
import com.bliblifuture.Invenger.exception.InvalidRequestException;
import com.bliblifuture.Invenger.model.inventory.Category;
import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.model.inventory.InventoryDocument;
import com.bliblifuture.Invenger.repository.InventoryDocRepository;
import com.bliblifuture.Invenger.repository.InventoryRepository;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.*;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.Invenger.response.viewDto.InventoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

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

    private DataTablesUtils<Inventory> dataTablesUtils;

    private LocalDateTime inventoriesLastUpdate = LocalDateTime.now();

    private void refreshInventoriesLastUpdate(){
        inventoriesLastUpdate = LocalDateTime.now();
    }

    private final static String PDF_TEMPLATE = "inventory/pdf_template";

    private final InventoryMapper mapper = Mappers.getMapper(InventoryMapper.class);


    public InventoryService(){
        dataTablesUtils = new DataTablesUtils<>(mapper);
    }


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
            boolean hasError = false;
            try{
                inventoryRepository.save(newInventory);
            }
            catch (DataIntegrityViolationException e){
                fileStorageService.deleteFile(
                        imgName,
                        FileStorageService.PathCategory.INVENTORY_PICT
                );
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

        this.refreshInventoriesLastUpdate();
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

        this.refreshInventoriesLastUpdate();

        return response;
    }

    public RequestResponse deleteInventory(int id){
        RequestResponse response = new RequestResponse();
        inventoryRepository.deleteById(id);
        response.setStatusToSuccess();
        response.setMessage("Item Deleted");

        this.refreshInventoriesLastUpdate();

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

    public long countRecord(){
        return inventoryRepository.count();
    }

    public DataTablesResult<InventoryDataTableResponse> getPaginatedDatatablesInventoryList(
            DataTablesRequest request){
        QuerySpec<Inventory> spec = dataTablesUtils.getQuerySpec(request);

        Page<Inventory> page;
        DataTablesResult<InventoryDataTableResponse> result = new DataTablesResult<>();
        System.out.println(spec.getSpecification());
        if(spec.getSpecification() == null){
            System.out.println("no filter");
            page = inventoryRepository.findAll(spec.getPageRequest());
        }
        else{
            System.out.println("has filter");
            page = inventoryRepository.findAll(spec.getSpecification(),spec.getPageRequest());
        }
        
        result.setListOfDataObjects(mapper.toInventoryDatatables(page.getContent()));
        result.setDraw(Integer.parseInt(request.getDraw()));
        result.setRecordsFiltered((int) page.getTotalElements());
        result.setRecordsTotal((int) this.countRecord());

        return result;
    }

    public SearchResponse getSearchedInventory(String query, Integer pageNum, Integer length){
        PageRequest pageRequest = PageRequest.of(pageNum,length);
        Specification<Inventory> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), "%" + query.toLowerCase() + "%")
            );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Inventory> page = inventoryRepository.findAll(specification,pageRequest);
        SearchResponse response = new SearchResponse();
        response.setResults(mapper.toSearchResultList(page.getContent()));
        response.setRecordsFiltered((int) page.getTotalElements());

        return response;
    }

    public RequestResponse insertInventories(MultipartFile file) throws InvalidRequestException {
        BufferedReader br;
        List<Inventory> inventories = new LinkedList<>();
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            line = br.readLine();
            String[] header = line.split(",");
            System.out.println("header");
            System.out.println(header);
            Category category = categoryRepository.findCategoryByName("/all");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Inventory inventory = new Inventory();
                for(int i=0; i<values.length; i++){
                    mapper.insertValueToObject(inventory,header[i],values[i]);
                }
                inventory.setCategory(category);
                inventories.add(inventory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidRequestException("File is invalid, Please check the instruction");
        }

        inventoryRepository.saveAll(inventories);
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        response.setMessage(inventories.size()+" data added to database");
        return response;
    }



}
