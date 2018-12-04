package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDataTableResponse {

    @Builder
    @Data
    public static class RowData{
        Integer pkey;
    }
    Integer id;

    @JsonProperty("DT_RowId")
    String rowId;

    String name;

    int price;

    int quantity;

    String category;

    String type;

}
