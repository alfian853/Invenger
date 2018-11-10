package com.bliblifuture.Invenger.ModelMapper.inventory;

import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.response.viewDto.InventoryDTO;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapperImpl implements InventoryMapper{
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
}
