package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.LinkedList;
import java.util.List;


@Data
public class CategoryEditResponse extends RequestResponse {

    @Data
    private class CategoryData {
        String name;
        Integer id;
        Integer parent_id;

        public CategoryData(Integer id, String name, Integer parent_id) {
            this.id = id;
            this.name = name;
            this.parent_id = parent_id;
        }
    }

    @JsonProperty("categories")
    @Setter(AccessLevel.NONE)
    List<CategoryData> categoryDataList = new LinkedList<>();

    public void addCategoryData(Integer id, String name, Integer parent_id){
        categoryDataList.add(
                new CategoryData(id,name,parent_id)
        );
    }

}
