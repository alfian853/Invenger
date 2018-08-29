package com.bliblifuture.Invenger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class Inventory extends AuditModel{

    @Id
    Integer id;

    String image,//image url
            name;

    int quantity;

    //in idr exchange rate
    int price;

    @Column(columnDefinition = "text")
    String description;


}
