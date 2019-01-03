package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.entity.lendment.LendmentStatus;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.invenger.response.jsonResponse.DataTablesResult;
import com.bliblifuture.invenger.response.jsonResponse.LendmentDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.impl.InventoryServiceImpl;
import com.bliblifuture.invenger.service.LendmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/lendment")
public class LendmentController {

    @Autowired
    LendmentService lendmentService;

    @Autowired
    AccountService accountService;

    @Autowired
    InventoryServiceImpl inventoryService;

    @GetMapping("/create")
    public String getAssignItemForm(Model model){
        if(accountService.currentUserIsAdmin()){
            return "lendment/lendment_create";
        }
        else{
            model.addAttribute("user", accountService.getProfile());
            return "lendment/lendment_create_basic";
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public RequestResponse assignItemToUser(@Valid @RequestBody LendmentCreateRequest request) {
        return lendmentService.createLendment(request);
    }

    @GetMapping("/all")
    public String getLendmentTable(Model model){
        model.addAttribute("statusList",LendmentStatus.getMap());
        if(accountService.currentUserIsAdmin()){
          model.addAttribute("lendments",lendmentService.getAll());
          return "lendment/lendment_list_admin";
        }
        else{
          model.addAttribute("lendments",lendmentService.getAllByUser());
          return "lendment/lendment_list_basic";
        }
    }

    @GetMapping("/requests")
    public String getLendmentQueueTable(Model model){
        model.addAttribute("status",LendmentStatus.getMap());
        model.addAttribute("lendments",lendmentService.getAllLendmentRequestOfSuperior());

        return "lendment/lendment_request_list";
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public RequestResponse doApprovement(@PathVariable("id") Integer lendmentId,
                                         @RequestParam(value = "approve")boolean isApprove) {
        return lendmentService.assignLendmentRequest(lendmentId,isApprove);
    }

    @GetMapping("/detail/{id}")
    public String getLendment(@PathVariable("id") Integer id,Model model) {

        LendmentDTO lendment = lendmentService.getLendmentDetailById(id);
        model.addAttribute("lendment", lendment);
        System.out.println(lendment.getStatus());
        if(accountService.currentUserIsAdmin()){
            if(!lendment.getStatus().equals(LendmentStatus.InLending.getDesc())){
                return "lendment/lendment_detail_basic";
            }
            return "lendment/lendment_detail_admin";
        }
        else{
            return "lendment/lendment_detail_basic";
        }

    }

    @PostMapping("/return")
    @ResponseBody
    public RequestResponse returnInventory(@Valid @RequestBody LendmentReturnRequest request) {
        return lendmentService.returnInventory(request);
    }

    @PostMapping("/handover/{id}")
    @ResponseBody
    public RequestResponse doHandOver(@PathVariable("id") Integer lendmentId) {
        return lendmentService.handOverOrderItems(lendmentId);
    }

    @GetMapping
    public String trackInventory(Model model,@RequestParam("having-item-id") Integer inventoryId){
        model.addAttribute("lendments",lendmentService.getInventoryLendment(inventoryId));
        return "lendment/lendment_inventory";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public DataTablesResult<LendmentDataTableResponse> getPaginatedLendments(HttpServletRequest servletRequest){
        DataTablesRequest request = new DataTablesRequest(servletRequest);
        return lendmentService.getDatatablesData(request);
    }

}
