package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InventoryService extends
        DatatablesService<DataTablesResult<InventoryDataTableResponse>>,
        SearchService<SearchResponse, SearchRequest> {
    List<InventoryDTO> getAll();
    InventoryDTO getById(Integer id);
    InventoryCreateResponse createInventory(InventoryCreateRequest request);
    RequestResponse updateInventory(InventoryEditRequest request);
    RequestResponse deleteInventory(int id);
    InventoryDocDownloadResponse downloadItemDetail(Integer id);
    RequestResponse insertInventories(MultipartFile file);
}
