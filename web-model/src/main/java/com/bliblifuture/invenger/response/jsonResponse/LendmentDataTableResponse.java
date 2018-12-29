package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LendmentDataTableResponse {
    @Builder
    @Data
    public static class RowData{
        Integer pkey;
    }
    Integer id;

    @JsonProperty("DT_RowId")
    String rowId;

    String username;

    @JsonProperty("createdAt")
    String createdAt;

    String status;
}
