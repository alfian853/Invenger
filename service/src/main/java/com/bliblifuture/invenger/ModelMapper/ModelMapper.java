package com.bliblifuture.invenger.ModelMapper;

import java.util.List;

public interface ModelMapper<DTO,ENTITY> {
    DTO toDto(ENTITY entity);
    List<DTO> toDtoList(List<ENTITY> entities);
}
