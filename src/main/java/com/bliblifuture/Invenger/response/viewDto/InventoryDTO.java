package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDTO {

    Integer id;

    String image;// image filename

    String name;

    int quantity;

    //in idr exchange rate
    int price;

    String category;
    Integer category_id;

    String description;

    String type;


}
