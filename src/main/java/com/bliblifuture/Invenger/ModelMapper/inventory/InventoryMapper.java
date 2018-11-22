package com.bliblifuture.Invenger.ModelMapper.inventory;

import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.response.jsonResponse.InventoryDataTableResponse;
import com.bliblifuture.Invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.Invenger.response.viewDto.InventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    InventoryDTO toInventoryDto(Inventory inventory);
    List<InventoryDTO> toInventoryDtoList(List<Inventory> inventories);

    List<InventoryDataTableResponse> toInventoryDatatables(List<Inventory> page);

    List<SearchItem> toSearchResultList(List<Inventory> inventories);
}
