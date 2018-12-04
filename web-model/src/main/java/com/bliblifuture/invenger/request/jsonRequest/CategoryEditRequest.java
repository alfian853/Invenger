package com.bliblifuture.invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CategoryEditRequest {

    @NotNull
    @JsonProperty("id")
    Integer id;

    @NotEmpty
    @JsonProperty("new_name")
    String newName;

    @NotNull
    @JsonProperty("new_parent_id")
    Integer newParentId;
}
