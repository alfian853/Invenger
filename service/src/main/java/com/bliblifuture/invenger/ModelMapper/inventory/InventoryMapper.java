package com.bliblifuture.invenger.ModelMapper.inventory;

import com.bliblifuture.invenger.ModelMapper.CriteriaPathMapper;
import com.bliblifuture.invenger.ModelMapper.FieldMapper;
import com.bliblifuture.invenger.model.inventory.Inventory;
import com.bliblifuture.invenger.response.jsonResponse.InventoryDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper extends CriteriaPathMapper, FieldMapper<Inventory> {

    InventoryDTO toInventoryDto(Inventory inventory);
    List<InventoryDTO> toInventoryDtoList(List<Inventory> inventories);

    List<InventoryDataTableResponse> toInventoryDatatables(List<Inventory> inventories);

    List<SearchItem> toSearchResultList(List<Inventory> inventories);
}
