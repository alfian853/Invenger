package com.bliblifuture.invenger.ModelMapper.category;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.response.jsonResponse.search_response.InventorySearchItem;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapperImpl implements CategoryMapper{


    @Override
    public CategoryDTO toDto(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .parent_id((category.getParent() != null)?category.getParent().getId():null)
                .build();
    }

    @Override
    public List<CategoryDTO> toDtoList(List<Category> categories) {
        return categories.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SearchItem> toSearchResultList(List<Category> categories) {
        List<SearchItem> responses = new LinkedList<>();
        SearchItem item;
        for(Category category : categories){
            item = new SearchItem();
            item.setId(category.getId());
            item.setText(category.getName());
            responses.add(item);
        }
        return responses;
    }

}
