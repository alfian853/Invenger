package com.bliblifuture.Invenger.request.formRequest;

import com.bliblifuture.Invenger.model.inventory.InventoryType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class InventoryCreateRequest {

    @NotEmpty(message = "Please fill name field")
    String name;

    @NotNull
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
