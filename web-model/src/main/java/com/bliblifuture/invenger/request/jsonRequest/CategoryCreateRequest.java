package com.bliblifuture.invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CategoryCreateRequest {

    @NotEmpty
    @JsonProperty("name")
    String name;

    @NotNull
    @JsonProperty("parent_id")
    Integer parentId;



}
