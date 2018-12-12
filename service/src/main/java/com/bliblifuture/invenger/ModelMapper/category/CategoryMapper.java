package com.bliblifuture.invenger.ModelMapper.category;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryDTO toCategoryDTO(Category category);

    List<CategoryDTO> toCategoryDtoList(List<Category> categories);

}
