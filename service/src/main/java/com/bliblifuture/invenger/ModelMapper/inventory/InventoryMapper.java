package com.bliblifuture.invenger.ModelMapper.inventory;

import com.bliblifuture.invenger.ModelMapper.DataTableMapper;
import com.bliblifuture.invenger.ModelMapper.FieldMapper;
import com.bliblifuture.invenger.ModelMapper.ModelMapper;
import com.bliblifuture.invenger.ModelMapper.SearchResultMapper;
import com.bliblifuture.invenger.entity.inventory.Inventory;
import com.bliblifuture.invenger.response.jsonResponse.InventoryDataTableResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.InventorySearchItem;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchItem;
import com.bliblifuture.invenger.response.viewDto.InventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper extends
        ModelMapper<InventoryDTO,Inventory>,
        DataTableMapper<InventoryDataTableResponse,Inventory>,
        SearchResultMapper<Inventory>,
        FieldMapper<Inventory> {


}
