package com.bliblifuture.Invenger.response;

import com.bliblifuture.Invenger.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategorCreateyResponse extends RequestResponse {

    @JsonProperty("new-category")
    Category category;

}
