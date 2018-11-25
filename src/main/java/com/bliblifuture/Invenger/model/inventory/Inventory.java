package com.bliblifuture.Invenger.model.inventory;

import com.bliblifuture.Invenger.model.AuditModel;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String image;//image filename

    @Column(unique = true)
    String name;

    @Min(value = 0)
    int quantity;

    //in idr exchange rate
    @Min(0)
    int price;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    @Column(columnDefinition = "text")
    String description;

    @NotEmpty
    String type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    @LazyToOne(value = LazyToOneOption.NO_PROXY)
    InventoryDocument document;

}
