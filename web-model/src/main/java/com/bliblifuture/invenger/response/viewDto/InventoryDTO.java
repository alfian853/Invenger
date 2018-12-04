package com.bliblifuture.invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDTO {

    Integer id;

    String image;// image filename

    String name;

    //in idr exchange rate
    int price;

    int quantity;

    String category;

    Integer category_id;

    String description;

    String type;


}
