package com.bliblifuture.invenger.request.formRequest;

import com.bliblifuture.invenger.entity.inventory.InventoryType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class InventoryEditRequest {

    @NotNull
    Integer id;

    String name;

    Integer price;

    Integer quantity;

    String description;

    MultipartFile pict;

    InventoryType type;

    Integer category_id;

}
