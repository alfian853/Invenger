package com.bliblifuture.Invenger.ModelMapper.category;

import com.bliblifuture.Invenger.model.inventory.Category;
import com.bliblifuture.Invenger.response.viewDto.CategoryDTO;

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
