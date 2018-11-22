package com.bliblifuture.Invenger.response.jsonResponse.search_response;

import lombok.Data;

@Data
public class InventorySearchItem extends SearchItem {
    Integer quantity;
    String category;
}
