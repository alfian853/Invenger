package com.bliblifuture.Invenger.ModelMapper.category;

import com.bliblifuture.Invenger.model.inventory.Category;
import com.bliblifuture.Invenger.response.viewDto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryDTO toCategoryDTO(Category category);

    List<CategoryDTO> toCategoryDtoList(List<Category> categories);

}
