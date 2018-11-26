package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.exception.InvalidRequestException;
import com.bliblifuture.Invenger.model.inventory.InventoryType;
import com.bliblifuture.Invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryCreateRequest;
import com.bliblifuture.Invenger.request.formRequest.InventoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.*;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.ItemCategoryService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Controller
public class InventoryController {

    @Autowired
    ItemCategoryService itemCategoryService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    UserService userService;

    @GetMapping("/inventory/all")
    public String getInventoryTable(Model model){
        model.addAttribute("categories",itemCategoryService.getAllItemCategory(false));
        model.addAttribute("itemTypes", InventoryType.getAllType());
        model.addAttribute("createItemForm",new InventoryCreateRequest());

        if(userService.currentUserIsAdmin()){
            return "inventory/inventory_list_admin";
        }
        else{
            return "inventory/inventory_list_basic";
        }
    }

    @PostMapping("/inventory/create")
    @ResponseBody
    public InventoryCreateResponse addNewInventory(@Valid @ModelAttribute InventoryCreateRequest request) throws Exception {
        return inventoryService.createInventory(request);
    }

    @PostMapping("/inventory/edit")
    @ResponseBody
    public RequestResponse editInventory(@Valid @ModelAttribute InventoryEditRequest request) throws Exception {
        return inventoryService.updateInventory(request);
    }

    @PostMapping("/inventory/delete/{id}")
    @ResponseBody
    public RequestResponse removeInventory(@PathVariable("id") Integer id){
        return inventoryService.deleteInventory(id);
    }

    @GetMapping("/inventory/detail/{id}")
    public String getInventoryDetail(Model model, @PathVariable("id") Integer id) throws Exception {
        model.addAttribute("inventory", inventoryService.getById(id));
        model.addAttribute("itemTypes", InventoryType.getAllType());
        if(userService.currentUserIsAdmin()){
            return "inventory/inventory_detail_admin";
        }
        else{
            return "inventory/inventory_detail_basic";
        }
    }

    @GetMapping("/inventory/detail/{id}/download")
    public String downloadInventoryDocument(@PathVariable("id") Integer id) throws Exception {
        InventoryDocDownloadResponse response = inventoryService.downloadItemDetail(id);
        return "redirect:"+response.getInventoryDocUrl();
    }

    @GetMapping("/datatables/inventory")
    @ResponseBody
    public DataTablesResult<InventoryDataTableResponse> getPaginatedInventories(
            HttpServletRequest servletRequest){
        DataTablesRequest request = new DataTablesRequest(servletRequest);
        return inventoryService.getPaginatedDatatablesInventoryList(request);
    }

    @GetMapping("/inventory/search")
    @ResponseBody
    public SearchResponse searchInventory(@RequestParam("search")String query,
                                          @RequestParam("page")Integer page,
                                          @RequestParam("length")Integer length){

        return inventoryService.getSearchedInventory(query,page,length);
    }

    @PostMapping("/inventory/upload")
    @ResponseBody
    public RequestResponse uploadInventories(@RequestParam("file") MultipartFile file) throws InvalidRequestException {
        return inventoryService.insertInventories(file);
    }

}
