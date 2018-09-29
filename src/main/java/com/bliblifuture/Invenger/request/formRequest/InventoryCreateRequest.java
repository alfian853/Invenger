package com.bliblifuture.Invenger.request.formRequest;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class InventoryCreateRequest {

    @NotEmpty(message = "Please fill name field")
    String name;

    MultipartFile photo_file;

    Integer category_id;

    Integer quantity;

    Integer price;

    String description;

}
