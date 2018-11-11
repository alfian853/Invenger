package com.bliblifuture.Invenger.response.jsonResponse;

import com.bliblifuture.Invenger.model.inventory.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryCreateResponse extends RequestResponse {

    @JsonProperty("new-category")
    Category category;

}
