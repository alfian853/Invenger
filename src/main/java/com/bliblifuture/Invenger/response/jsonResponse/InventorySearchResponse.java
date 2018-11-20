package com.bliblifuture.Invenger.response.jsonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySearchResponse extends SearchResponse {
    String category;
}
