package com.bliblifuture.invenger.entity.inventory;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CategoryWithChildId implements Cloneable{

    private Integer id;
    private String name;

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonIgnore
    private List<Integer> childsId;

    public CategoryWithChildId(Integer id, String name, Integer parentId, List<Integer> childsId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.childsId = childsId;
    }
}
