package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Builder
@Data
public class LendmentDTO {

    Integer id;
    String username;
    String status;
    Date orderDate;
}
