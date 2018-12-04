package com.bliblifuture.invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Builder
@Data
public class LendmentDTO {

    Integer id;
    String username;
    String status;
    Date orderDate;

    @Data
    @Builder
    public static class Detail{
        Integer inventoryId;
        String inventoryName;
        Boolean isReturned;
        Integer quantity;
        String returnDate;
    }

    List<Detail> details;

}
