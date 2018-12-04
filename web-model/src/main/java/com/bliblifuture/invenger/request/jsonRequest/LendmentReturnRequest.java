package com.bliblifuture.invenger.request.jsonRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LendmentReturnRequest {
    @NotNull
    @JsonProperty("lendment_id")
    Integer lendmentId;

    @NotEmpty
    @JsonProperty("inventories_id")
    List<Integer> inventoriesId;
}
