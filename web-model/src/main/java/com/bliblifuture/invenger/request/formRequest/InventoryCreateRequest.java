package com.bliblifuture.invenger.request.formRequest;

import com.bliblifuture.invenger.entity.inventory.InventoryType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class InventoryCreateRequest {

    @NotEmpty(message = "Please fill name field")
    String name;

    MultipartFile photo_file;

    @NotNull
    Integer category_id;

    @NotNull
    Integer quantity;

    Integer price;

    String description;

    @NotNull
    InventoryType type;

}
