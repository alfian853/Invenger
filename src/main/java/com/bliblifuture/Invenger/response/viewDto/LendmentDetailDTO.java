package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LendmentDetailDTO {

    Integer inventoryId;
    String inventoryName;
    Boolean isReturned;
    Integer quantity;
    String returnDate;

}
