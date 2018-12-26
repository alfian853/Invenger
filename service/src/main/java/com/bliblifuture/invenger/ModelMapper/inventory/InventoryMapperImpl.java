package com.bliblifuture.invenger.ModelMapper.inventory;

import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.entity.inventory.InventoryType;
import com.bliblifuture.invenger.response.jsonResponse.InventoryDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.InventorySearchItem;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapperImpl implements InventoryMapper{

    @Override
    public InventoryDTO toDto(Inventory inventory) {
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
    public List<InventoryDTO> toDtoList(List<Inventory> inventories) {
        return inventories.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<InventoryDataTableResponse> toDataTablesDtoList(List<Inventory> inventories) {

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
        else if(field.equals("inventory_id")){
            return root.get("id");
        }
        return root.get(field);
    }

    @Override
    public void insertValueToObject(Inventory object, String field, String value) throws Exception {
        switch (field){
            case "name":
                object.setName(value);
                break;
            case "price":
                object.setPrice(Integer.parseInt(value));
                break;
            case "quantity":
                object.setQuantity(Integer.parseInt(value));
                break;
            case "description":
                object.setDescription(value);
                break;
            case "type":
                object.setType(InventoryType.valueOf(value).toString());
                break;
             default:
                 throw new Exception("field \""+field+"\" not found");
        }
    }
}
