package com.bliblifuture.invenger.ModelMapper.category;

import com.bliblifuture.invenger.model.inventory.Category;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapperImpl implements CategoryMapper{


    @Override
    public CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .parent_id((category.getParent() != null)?category.getParent().getId():null)
                .build();
    }

    @Override
    public List<CategoryDTO> toCategoryDtoList(List<Category> categories) {
        return categories.stream().map(this::toCategoryDTO).collect(Collectors.toList());
    }

}
