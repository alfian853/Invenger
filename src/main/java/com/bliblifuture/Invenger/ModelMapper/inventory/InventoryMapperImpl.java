package com.bliblifuture.Invenger.ModelMapper.inventory;

import com.bliblifuture.Invenger.Utils.PathMapper;
import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.response.jsonResponse.InventoryDataTableResponse;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.InventorySearchItem;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.Invenger.response.viewDto.InventoryDTO;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapperImpl implements InventoryMapper, PathMapper {

    @Override
    public InventoryDTO toInventoryDto(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .image(inventory.getImage())
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice())
                .description(inventory.getDescription())
                .type(inventory.getType())
                .category_id(inventory.getCategory().getId())
                .category(inventory.getCategory().getName())
                .build();
    }

    @Override
    public List<InventoryDTO> toInventoryDtoList(List<Inventory> inventories) {
        return inventories.stream().map(this::toInventoryDto).collect(Collectors.toList());
    }

    @Override
    public List<InventoryDataTableResponse> toInventoryDatatables(List<Inventory> inventories) {

        List<InventoryDataTableResponse> responses = new LinkedList<>();

        for(Inventory inventory : inventories){
            responses.add(InventoryDataTableResponse.builder()
                    .id(inventory.getId())
                    .rowId("row_"+inventory.getId())
                    .name(inventory.getName())
                    .quantity(inventory.getQuantity())
                    .price(inventory.getPrice())
                    .type(inventory.getType())
                    .category(inventory.getCategory().getName())
                    .build()
            );
        }

        return responses;
    }

    @Override
    public List<SearchItem> toSearchResultList(List<Inventory> inventories) {
        List<SearchItem> responses = new LinkedList<>();
        InventorySearchItem item;
        for(Inventory inventory : inventories){
            item = new InventorySearchItem();
            item.setId(inventory.getId());
            item.setText(inventory.getName());
            item.setCategory(inventory.getCategory().getName());
            item.setQuantity(inventory.getQuantity());
            responses.add(item);
        }
        return responses;
    }

    @Override
    public Path getPathFrom(Root root, String field) {
        if(field.equals("category")){
            return root.get("category").get("name");
        }
        return root.get(field);
    }
}
