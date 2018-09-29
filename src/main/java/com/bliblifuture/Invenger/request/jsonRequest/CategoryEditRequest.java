package com.bliblifuture.Invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryEditRequest {

    @JsonProperty("id")
    Integer id;

    @JsonProperty("new_name")
    String newName;
}
