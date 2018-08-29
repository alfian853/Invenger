package com.bliblifuture.Invenger.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "positions")
public class Position {

    @Id
    Integer id;

    String name;

    @Column(columnDefinition = "text")
    String description;

}
