package com.bliblifuture.invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LendmentCreateRequest {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item{
        @NotNull
        Integer id;

        @Min(1)
        @NotNull
        Integer quantity;
    }

    @NotNull
    @JsonProperty("user_id")
    Integer userId;

    @Valid
    @NotEmpty
    List<Item> items;

    public Integer getChildIdAt(int index){
        return items.get(index).getId();
    }

    public Integer getChildQuantityAt(int index){
        return items.get(index).getQuantity();
    }


}
