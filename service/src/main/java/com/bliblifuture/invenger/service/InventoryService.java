package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.InventoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.InventoryDocDownloadResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InventoryService extends DatatablesService, SearchService {
    List<InventoryDTO> getAll();
    InventoryDTO getById(Integer id);
    InventoryCreateResponse createInventory(InventoryCreateRequest request);
    RequestResponse updateInventory(InventoryEditRequest request);
    RequestResponse deleteInventory(int id);
    InventoryDocDownloadResponse downloadItemDetail(Integer id);
    RequestResponse insertInventories(MultipartFile file);
}
