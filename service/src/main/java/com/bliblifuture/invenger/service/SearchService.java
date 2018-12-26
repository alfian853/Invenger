package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;

public interface SearchService<RESPONSE extends SearchResponse, REQUEST extends SearchRequest> {
    RESPONSE getSearchResult(REQUEST request);
}
