package com.bliblifuture.invenger.ModelMapper.lendment;

import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LendmentMapper {

    LendmentDTO toLendmentDTO(Lendment lendment);

    LendmentDTO toLendmentWithDetailDTO(Lendment lendment);

    List<LendmentDTO> toLendmentDtoList(List<Lendment> lendments, boolean isWithDetails);

}
