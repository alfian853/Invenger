package com.bliblifuture.Invenger.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String name;

    @Column(columnDefinition = "text")
    String description;

}
