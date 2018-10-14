package com.bliblifuture.Invenger.request.formRequest;

import com.bliblifuture.Invenger.model.InventoryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

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
