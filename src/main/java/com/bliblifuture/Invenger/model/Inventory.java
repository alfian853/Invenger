package com.bliblifuture.Invenger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class Inventory extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String image,//image url
            name;

    int quantity;

    //in idr exchange rate
    int price;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    @Column(columnDefinition = "text")
    String description;

    @Enumerated(EnumType.STRING)
    InventoryType type;

}
