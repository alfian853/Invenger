package com.bliblifuture.Invenger.response.viewDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    Integer id;
    Integer parent_id;
    String name;

}
