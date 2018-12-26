package com.bliblifuture.invenger.ModelMapper.lendment;

import com.bliblifuture.invenger.entity.lendment.Lendment;
import com.bliblifuture.invenger.entity.lendment.LendmentDetail;
import com.bliblifuture.invenger.response.jsonResponse.LendmentDatatableResponse;
import com.bliblifuture.invenger.response.viewDto.LendmentDTO;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
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

    @Override
    public List<LendmentDatatableResponse> toLendmentDatatable(List<Lendment> lendments) {
        List<LendmentDatatableResponse> responses = new LinkedList<>();

        for(Lendment lendment : lendments){
            responses.add(LendmentDatatableResponse.builder()
                    .id(lendment.getId())
                    .rowId("row_"+lendment.getId())
                    .username(lendment.getUser().getUsername())
                    .createdAt(lendment.getCreatedAt().toString())
                    .status(lendment.getStatus())
                    .build()
            );
        }

        return responses;
    }

    @Override
    public Path getPathFrom(Root root, String field) {
        switch (field){
            case "username":
                return root.get("user").get("username");
            default:
                return root.get(field);
        }
    }
}
