package com.bliblifuture.Invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AssignItemsToUserRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Item{
        Integer id;
        Integer quantity;
    }

    @JsonProperty("user_id")
    Integer userId;

    List<Item> items;

    Integer getChildIdAt(int index){
        return items.get(index).getId();
    }

    Integer getChildQuantityAt(int index){
        return items.get(index).getQuantity();
    }


}
