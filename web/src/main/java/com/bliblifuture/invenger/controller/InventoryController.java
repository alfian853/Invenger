package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.entity.inventory.InventoryType;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
@RequestMapping("/inventory")
public class InventoryController {
    
    @Autowired
    InventoryService inventoryService;

    @Autowired
    AccountService accountService;

    @GetMapping("/all")
    public String getInventoryTable(Model model){
        model.addAttribute("itemTypes", InventoryType.getAllType());
        model.addAttribute("createItemForm",new InventoryCreateRequest());

        if(accountService.currentUserIsAdmin()){
            return "inventory/inventory_list_admin";
        }
        else{
            return "inventory/inventory_list_basic";
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request) {
        return inventoryService.createInventory(request);
    }

    @PostMapping("/edit")
    @ResponseBody
    public RequestResponse editInventory(@Valid @ModelAttribute InventoryEditRequest request) {
        return inventoryService.updateInventory(request);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public RequestResponse removeInventory(@PathVariable("id") Integer id){
        return inventoryService.deleteInventory(id);
    }

    @GetMapping("/detail/{id}")
    public String getInventoryDetail(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("inventory", inventoryService.getById(id));
        model.addAttribute("itemTypes", InventoryType.getAllTypeAsString());
        if(accountService.currentUserIsAdmin()){
            return "inventory/inventory_detail_admin";
        }
        else{
            return "inventory/inventory_detail_basic";
        }
    }

    @GetMapping("/detail/{id}/download")
    public String downloadInventoryDocument(@PathVariable("id") Integer id) {
        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(id);
        return "redirect:"+response.getInventoryDocUrl();
    }

    @GetMapping("/datatables")
    @ResponseBody
    public DataTablesResult<InventoryDataTableResponse> getPaginatedInventories(
            HttpServletRequest servletRequest){
        DataTablesRequest request = new DataTablesRequest(servletRequest);
        return inventoryService.getDatatablesData(request);
    }

    @GetMapping("/search")
    @ResponseBody
    public SearchResponse searchInventory(@RequestParam("search")String query,
                                          @RequestParam("page")Integer page,
                                          @RequestParam("length")Integer length){

        return inventoryService.getSearchResult(SearchRequest.builder()
                .query(query).pageNum(page).length(length).build());
    }

    @PostMapping("/upload")
    @ResponseBody
    public RequestResponse uploadInventories(@RequestParam("file") MultipartFile file) {
        return inventoryService.insertInventories(file);
    }

}
