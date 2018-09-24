package com.bliblifuture.Invenger.controller;

import com.bliblifuture.Invenger.model.Inventory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
public class InventoryController {

    @GetMapping("/modal")
    public String getModal(Model model, HttpServletRequest request){
        return "modal";
    }

}
