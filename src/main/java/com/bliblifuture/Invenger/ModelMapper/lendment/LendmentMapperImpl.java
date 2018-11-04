package com.bliblifuture.Invenger.ModelMapper.lendment;

import com.bliblifuture.Invenger.model.lendment.Lendment;
import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.response.viewDto.LendmentDTO;
import com.bliblifuture.Invenger.response.viewDto.LendmentDetailDTO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class LendmentMapperImpl implements LendmentMapper {

    @Override
    public LendmentDTO toLendmentDTO(Lendment lendment) {
        return LendmentDTO
                .builder()
                .id(lendment.getId())
                .username(lendment.getUser().getUsername())
                .status(lendment.getStatus())
                .orderDate(lendment.getCreatedAt())
                .build();
    }

    @Override
    public LendmentDetailDTO toLendmentDetailDTO(LendmentDetail lendment) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return LendmentDetailDTO
                .builder()
                .inventoryId(lendment.getInventory().getId())
                .inventoryName(lendment.getInventory().getName())
                .quantity(lendment.getQuantity())
                .isReturned(lendment.isReturned())
                .returnDate(lendment.getReturnDate() != null ? format.format(lendment.getReturnDate()) : null)
                .build();
    }


    @Override
    public List<LendmentDTO> toLendmentDtoList(List<Lendment> lendments) {
        return lendments.stream().map(this::toLendmentDTO).collect(Collectors.toList());
    }

    @Override
    public List<LendmentDetailDTO> toLendmentDetailDtoList(List<LendmentDetail> lendmentDetails) {
        return lendmentDetails.stream().map(this::toLendmentDetailDTO).collect(Collectors.toList());
    }


}
