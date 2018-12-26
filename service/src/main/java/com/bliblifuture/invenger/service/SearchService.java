package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;

public interface SearchService<RESPONSE extends SearchResponse> {
    RESPONSE getSearchResult(String query, int pageNum, int length);
}
