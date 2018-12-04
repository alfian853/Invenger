package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HandOverResponse extends RequestResponse {

    @JsonProperty("lendment_status")
    String lendmentStatus;

}
