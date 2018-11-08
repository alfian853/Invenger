package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.lendment.LendmentStatus;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.LendmentReturnRequest;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.InventoryService;
import com.bliblifuture.Invenger.service.LendmentService;
import com.bliblifuture.Invenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class LendmentController {

    @Autowired
    LendmentService lendmentService;

    @Autowired
    UserService userService;

    @Autowired
    InventoryService inventoryService;

    @GetMapping("lendment/create")
    public String getAssignItemForm(Model model){
        model.addAttribute("users",userService.getAll());
        return "lendment/lendment_create";
    }

    @PostMapping("lendment/create")
    @ResponseBody
    public RequestResponse assignItemToUser(@Valid @RequestBody LendmentCreateRequest request){
        try {
            return lendmentService.createLendment(request);
        } catch (Exception e) {
            e.printStackTrace();
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @GetMapping("lendment/all")
    public String getLendmentTable(Model model){

        model.addAttribute("status",LendmentStatus.getMap());
      if(userService.currentUserIsAdmin()){
          model.addAttribute("lendments",lendmentService.getAll());
          return "lendment/lendment_list_admin";
      }
      else{
          model.addAttribute("lendments",lendmentService.getAllByUser());
          return "lendment/lendment_list_basic";
      }

    }

    @GetMapping("lendment/requests")
    public String getLendmentQueueTable(Model model){
        model.addAttribute("status",LendmentStatus.getMap());
        model.addAttribute("lendments",lendmentService.getAllLendmentRequest());

        return "lendment/lendment_request_list";
    }

    @PostMapping("lendment/approve/{id}")
    @ResponseBody
    public RequestResponse doApprovement(@PathVariable("id") Integer lendmentId){
        try {
            return lendmentService.approveLendmentRequest(lendmentId);
        } catch (Exception e) {
            e.printStackTrace();
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @GetMapping("lendment/detail/{id}")
    public String getLendment(@PathVariable("id") Integer id,Model model){

        model.addAttribute("lendment_detail",
                lendmentService.getLendmentDetailById(id));
        model.addAttribute("lendment_id",id);
        if(userService.currentUserIsAdmin()){
            return "lendment/lendment_detail_admin";
        }
        else{
            return "lendment/lendment_detail_basic";
        }

    }

    @PostMapping("lendment/return")
    @ResponseBody
    public RequestResponse returnInventory(@Valid @RequestBody LendmentReturnRequest request){
        System.out.println(request);
        try {
            return lendmentService.returnInventory(request);
        } catch (Exception e) {
            e.printStackTrace();
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }

    @PostMapping("lendment/handover/{id}")
    @ResponseBody
    public RequestResponse doHandOver(@PathVariable("id") Integer lendmentId){
        try {
            return lendmentService.handOverOrderItems(lendmentId);
        } catch (Exception e) {
            e.printStackTrace();
            RequestResponse response = new RequestResponse();
            response.setStatusToFailed();
            return response;
        }
    }


}
