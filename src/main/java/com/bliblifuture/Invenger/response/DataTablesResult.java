package com.bliblifuture.Invenger.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataTablesResult<T> {

    private String draw;

    private Integer recordsFiltered;

    private Integer recordsTotal;

    @JsonProperty("data")
    List<T> listOfDataObjects;

    public void setListOfDataObjects(List<T> listOfDataObjects){
        this.listOfDataObjects = listOfDataObjects;
        recordsFiltered = listOfDataObjects.size();
    }

}