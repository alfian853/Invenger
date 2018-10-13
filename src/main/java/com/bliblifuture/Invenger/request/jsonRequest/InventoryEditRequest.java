package com.bliblifuture.Invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InventoryEditRequest {
    @JsonProperty("id")
    Integer id;

    @JsonProperty("new_name")
    String newName;

    @JsonProperty("new_price")
    String newPrice;

    @JsonProperty("new_quantity")
    String newQuantity;

    @JsonProperty("new_description")
    String newDescription;

    @JsonProperty("new_image")
    MultipartFile newImage;

    @JsonProperty("new_category_id")
    Integer newCategoryId;
}
