package com.bliblifuture.invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class PositionDTO {

    Integer id;

    @NotEmpty
    String name;

    @NotNull
    Integer level;

}
