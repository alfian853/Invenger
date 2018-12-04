package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LendmentReturnResponse extends RequestResponse {

    @JsonProperty("return_date")
    String returnDate;

}
