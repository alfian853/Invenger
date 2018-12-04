package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDocDownloadResponse extends RequestResponse{

    @JsonProperty("doc_url")
    String inventoryDocUrl;

}
