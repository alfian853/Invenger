package com.bliblifuture.invenger.ModelMapper;

import java.util.List;

public interface ModelMapper<DTO,ENTITY> {
    DTO toInventoryDto(ENTITY entity);
    List<DTO> toInventoryDtoList(List<ENTITY> entities);
}
