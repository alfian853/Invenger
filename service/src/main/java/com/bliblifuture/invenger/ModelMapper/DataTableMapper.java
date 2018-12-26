package com.bliblifuture.invenger.ModelMapper;

import java.util.List;

public interface DataTableMapper<DTO, ENTITY> extends CriteriaPathMapper {
    List<DTO> toDatatablesDtoList(List<ENTITY> entities);
}
