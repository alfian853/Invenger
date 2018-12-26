package com.bliblifuture.invenger.service;


import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;

import java.util.List;

public interface ItemCategoryService extends SearchService<SearchResponse, SearchRequest>{

    CategoryEditResponse updateCategory(CategoryEditRequest request);
    CategoryCreateResponse createCategory(CategoryCreateRequest request);
    RequestResponse deleteCategory(int id);
    List<CategoryDTO> getAllItemCategory(boolean fetchParent);
}
