package com.bliblifuture.invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @JsonProperty("name")
    String name;

    @JsonProperty("parent_id")
    Integer parentId;



}
