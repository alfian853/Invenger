package com.bliblifuture.invenger.response.jsonResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataTablesResult<T> {

    private Integer draw;

    private Integer recordsFiltered;

    private Integer recordsTotal;

    @JsonProperty("data")
    List<T> listOfDataObjects;

    public void setListOfDataObjects(List<T> listOfDataObjects){
        this.listOfDataObjects = listOfDataObjects;
        recordsFiltered = listOfDataObjects.size();
    }

}