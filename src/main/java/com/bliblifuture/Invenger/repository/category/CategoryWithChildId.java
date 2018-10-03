package com.bliblifuture.Invenger.repository.category;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryWithChildId {

    private Integer id;
    private String name;

    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonIgnore
    private List<Integer> childsId;

    public CategoryWithChildId(Integer id, String name, Integer parendId, List<Integer> childsId) {
        this.id = id;
        this.name = name;
        this.parentId = parendId;
        this.childsId = childsId;
    }
}
