package com.bliblifuture.invenger.ModelMapper.lendment;

import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.entity.lendment.LendmentDetail;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
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
    public LendmentDTO toLendmentWithDetailDTO(Lendment lendment) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        LendmentDTO result =  LendmentDTO
                .builder()
                .id(lendment.getId())
                .username(lendment.getUser().getUsername())
                .status(lendment.getStatus())
                .orderDate(lendment.getCreatedAt())
                .details(new LinkedList<>())
                .build();

        for(LendmentDetail detail : lendment.getLendmentDetails()){
            result.getDetails().add(
                LendmentDTO.Detail.builder()
                .inventoryId(detail.getInventory().getId())
                .inventoryName(detail.getInventory().getName())
                .quantity(detail.getQuantity())
                .isReturned(detail.isReturned())
                .returnDate(detail.getReturnDate() != null ? format.format(detail.getReturnDate()) : null)
                .build()
            );
        }

        return result;
    }


    @Override
    public List<LendmentDTO> toLendmentDtoList(List<Lendment> lendments, boolean isWithDetails) {

        return lendments.stream().map(
                (isWithDetails) ? this::toLendmentWithDetailDTO : this::toLendmentDTO)
                .collect(Collectors.toList());
    }

}
