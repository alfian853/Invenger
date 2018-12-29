package com.bliblifuture.invenger.ModelMapper.category;

import com.bliblifuture.invenger.ModelMapper.ModelMapper;
import com.bliblifuture.invenger.ModelMapper.SearchResultMapper;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper extends
        ModelMapper<CategoryDTO,Category>,
        SearchResultMapper<Category> {

}
