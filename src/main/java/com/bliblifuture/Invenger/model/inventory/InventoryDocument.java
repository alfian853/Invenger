package com.bliblifuture.Invenger.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories_document")
public class InventoryDocument{

    @Id
    Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "inventory_id",referencedColumnName = "id")
    Inventory inventory;

    String fileName;

    @Column(nullable = false)
    Date inventoryLastUpdate;

}