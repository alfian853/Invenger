package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PositionCreateResponse extends RequestResponse{
    @JsonProperty("position_id")
    Integer positionId;
}
