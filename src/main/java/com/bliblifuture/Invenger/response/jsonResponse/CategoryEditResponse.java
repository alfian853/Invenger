package com.bliblifuture.Invenger.response.jsonResponse;

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

        public CategoryData(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @JsonProperty("categories")
    @Setter(AccessLevel.NONE)
    List<CategoryData> categoryDataList = new LinkedList<>();

    public void addCategoryData(Integer id, String name){
        categoryDataList.add(
                new CategoryData(id,name)
        );
    }

}
