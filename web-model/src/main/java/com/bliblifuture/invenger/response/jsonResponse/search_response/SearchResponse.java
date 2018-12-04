package com.bliblifuture.invenger.response.jsonResponse.search_response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {

    List<SearchItem> results = new LinkedList<>();
    protected Integer recordsFiltered;

}
