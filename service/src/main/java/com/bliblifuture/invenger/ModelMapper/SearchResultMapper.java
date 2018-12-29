package com.bliblifuture.invenger.ModelMapper;

import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;

import java.util.List;

public interface SearchResultMapper<ENTITY> {
    List<SearchItem> toSearchResultList(List<ENTITY> entities);
}
