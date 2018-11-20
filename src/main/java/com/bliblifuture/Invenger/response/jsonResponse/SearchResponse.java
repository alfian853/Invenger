package com.bliblifuture.Invenger.response.jsonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {

    @Builder
    @Data
    public static class Item{
        Integer id;
        String text;
    }
    List<Item> results = new ArrayList<>();

    Integer recordsFiltered;

}
