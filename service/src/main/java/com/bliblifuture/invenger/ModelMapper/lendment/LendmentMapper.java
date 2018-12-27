package com.bliblifuture.invenger.ModelMapper.lendment;

import com.bliblifuture.invenger.ModelMapper.DataTableMapper;
import com.bliblifuture.invenger.ModelMapper.ModelMapper;
import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.response.jsonResponse.LendmentDataTableResponse;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LendmentMapper extends
        ModelMapper<LendmentDTO,Lendment>,
        DataTableMapper<LendmentDataTableResponse,Lendment>{


    LendmentDTO toLendmentWithDetailDTO(Lendment lendment);

    List<LendmentDTO> toDtoList(List<Lendment> lendments, boolean isWithDetails);
}
