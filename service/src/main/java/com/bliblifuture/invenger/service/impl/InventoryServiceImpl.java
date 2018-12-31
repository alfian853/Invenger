package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.ModelMapper.inventory.InventoryMapper;
import com.bliblifuture.invenger.Utils.DataTablesUtils;
import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.Utils.QuerySpec;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.DefaultRuntimeException;
import com.bliblifuture.invenger.exception.DuplicateEntryException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.inventory.InventoryDocument;
import com.bliblifuture.invenger.repository.InventoryDocRepository;
import com.bliblifuture.invenger.repository.InventoryRepository;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import com.bliblifuture.invenger.service.FileStorageService;
import com.bliblifuture.invenger.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class InventoryServiceImpl implements InventoryService {

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

    private final static String PDF_TEMPLATE_PATH = "inventory/pdf_template";

    private final InventoryMapper mapper = Mappers.getMapper(InventoryMapper.class);


    public InventoryServiceImpl(){
        dataTablesUtils = new DataTablesUtils<>(mapper);
    }


    @Override
    public List<InventoryDTO> getAll(){
        return mapper.toDtoList(inventoryRepository.findAllFetchCategory());
    }

    @Override
    public InventoryDTO getById(Integer id) {
        Inventory inventory = inventoryRepository.findInventoryById(id);
        System.out.println(inventory);
        if(inventory == null){
            throw new DataNotFoundException("Inventory Not Found");
        }

        return mapper.toDto(inventory);
    }

    @Override
    public InventoryCreateResponse createInventory(InventoryCreateRequest request){
        InventoryCreateResponse response = new InventoryCreateResponse();
        Category newCategory = new Category();
        newCategory.setId(request.getCategory_id());

        Inventory newInventory = new Inventory();
        newInventory.setName(request.getName());
        newInventory.setQuantity(request.getQuantity());
        newInventory.setPrice(request.getPrice());

        newInventory.setDescription(request.getDescription());
        newInventory.setCategory(newCategory);
        newInventory.setType(request.getType().toString());

        String imgName = null;

        if(request.getPhoto_file() != null) {
            imgName = UUID.randomUUID().toString().replace("-", "") +
                    "." + FilenameUtils.getExtension(request.getPhoto_file().getOriginalFilename());

            if (!fileStorageService.storeFile(
                    request.getPhoto_file(),
                    imgName,
                    FileStorageService.PathCategory.INVENTORY_PICT)
            ){
                throw new DefaultRuntimeException("Error when storing item picture");
            }
            newInventory.setImage(imgName);

        }

        try{
            inventoryRepository.save(newInventory);
        }
        catch (DataIntegrityViolationException e){
            e.printStackTrace();
            if(!newInventory.getImage().equals("default-item.jpg") && imgName != null){
                fileStorageService.deleteFile(
                        imgName,
                        FileStorageService.PathCategory.INVENTORY_PICT
                );
            }
            if(e.getLocalizedMessage().contains("duplicate")){
                throw new DuplicateEntryException("Inventory name already exist");
            }
        }
        response.setStatusToSuccess();

        response.setInventory_id(newInventory.getId());
        response.setMessage("New item added to table");

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RequestResponse updateInventory(InventoryEditRequest request) {

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
                throw new DefaultRuntimeException("Internal Server Error!");
            }

        }

        inventoryRepository.save(inventory);

        return response;
    }

    @Override
    public RequestResponse deleteInventory(int id){
        RequestResponse response = new RequestResponse();
        InventoryDocument doc = inventoryDocRepository.findInventoryDocumentById(id);
        if(doc != null){
            fileStorageService.deleteFile(doc.getFileName(), FileStorageService.PathCategory.INVENTORY_PDF);
            inventoryDocRepository.deleteById(id);
        }

        inventoryRepository.deleteById(id);
        response.setStatusToSuccess();
        response.setMessage("Item Deleted");

        return response;
    }

    @Override
    public InventoryDocDownloadResponse downloadItemDetail(Integer id) {

        InventoryDocDownloadResponse response = new InventoryDocDownloadResponse();

        Inventory inventory = inventoryRepository.findInventoryById(id);
        if(inventory == null){
            throw new DataNotFoundException("Inventory Not Found");
        }
        InventoryDocument doc = inventoryDocRepository.findInventoryDocumentById(id);
        if(doc == null || !doc.getInventoryLastUpdate().equals(inventory.getUpdatedAt()) ){

            InventoryDTO inventoryDTO = mapper.toDto(inventory);

            ObjectMapper oMapper = new ObjectMapper();
            Map templateMap = oMapper.convertValue(inventoryDTO, Map.class);

            String filename = fileStorageService.createPdfFromTemplate(
                    myUtils.getRandomFileName("pdf"),
                    PDF_TEMPLATE_PATH,
                    templateMap,
                    FileStorageService.PathCategory.INVENTORY_PDF
            );

            if(filename != null){
                //update doc
                if(doc != null){
                    fileStorageService.deleteFile(
                            doc.getFileName(),
                            FileStorageService.PathCategory.INVENTORY_PDF
                    );
                    doc.setFileName(filename);
                    doc.setInventoryLastUpdate(inventory.getUpdatedAt());
                }
                else{//create doc
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

    @Override
    public DataTablesResult<InventoryDataTableResponse> getDatatablesData(
            DataTablesRequest request){
        QuerySpec<Inventory> spec = dataTablesUtils.getQuerySpec(request);

        Page<Inventory> page;
        DataTablesResult<InventoryDataTableResponse> result = new DataTablesResult<>();

        if(spec.getSpecification() == null){
            page = inventoryRepository.findAll(spec.getPageRequest());
        }
        else{
            page = inventoryRepository.findAll(spec.getSpecification(),spec.getPageRequest());
        }
        
        result.setListOfDataObjects(mapper.toDataTablesDtoList(page.getContent()));
        result.setDraw(Integer.parseInt(request.getDraw()));
        result.setRecordsFiltered((int) page.getTotalElements());
        result.setRecordsTotal(result.getRecordsFiltered());

        return result;
    }

    @Override//String query, int pageNum, int length
    public SearchResponse getSearchResult(SearchRequest request){
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getLength());
        Specification<Inventory> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), "%" +
                            request.getQuery().toLowerCase() + "%")
            );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Inventory> page = inventoryRepository.findAll(specification,pageRequest);
        SearchResponse response = new SearchResponse();
        response.setResults(mapper.toSearchResultList(page.getContent()));
        response.setRecordsFiltered((int) page.getTotalElements());

        return response;
    }

    @Override
    public RequestResponse insertInventories(MultipartFile file) {
        BufferedReader br;
        List<Inventory> inventories = new LinkedList<>();
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            line = br.readLine();
            String[] header = line.split(",");

            Category category = categoryRepository.findCategoryByName("/all");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Inventory inventory = new Inventory();
                for(int i=0; i<values.length; i++){
                    mapper.insertValueToObject(inventory,header[i],values[i]);
                }
                inventory.setCategory(category);
                inventory.setImage("default-item.jpg");
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
