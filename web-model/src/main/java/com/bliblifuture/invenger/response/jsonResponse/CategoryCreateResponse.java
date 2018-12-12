package com.bliblifuture.invenger.response.jsonResponse;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryCreateResponse extends RequestResponse {

    @JsonProperty("new-category")
    Category category;

}
