package com.bliblifuture.Invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LendmentHandOverRequest {

    @JsonProperty("lendment_id")
    Integer lendmentId;

}
