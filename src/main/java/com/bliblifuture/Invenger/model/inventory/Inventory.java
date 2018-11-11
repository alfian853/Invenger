package com.bliblifuture.Invenger.model.inventory;

import com.bliblifuture.Invenger.model.AuditModel;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "inventories")
public class Inventory extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String image;//image filename
    String name;

    int quantity;

    //in idr exchange rate
    int price;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    @Column(columnDefinition = "text")
    String description;

    String type;

    @OneToOne(mappedBy = "inventory",fetch = FetchType.LAZY, optional = false)
    InventoryDocument document;

}
