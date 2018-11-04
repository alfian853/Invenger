package com.bliblifuture.Invenger.ModelMapper.lendment;

import com.bliblifuture.Invenger.model.lendment.Lendment;
import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.Invenger.response.viewDto.LendmentDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LendmentMapper {

    LendmentDTO toLendmentDTO(Lendment lendment);

    LendmentDetailDTO toLendmentDetailDTO(LendmentDetail lendment);

    List<LendmentDTO> toLendmentDtoList(List<Lendment> lendments);

    List<LendmentDetailDTO> toLendmentDetailDtoList(List<LendmentDetail> lendmentDetails);



}
