package com.bliblifuture.invenger.entity.inventory;

import com.bliblifuture.invenger.entity.AuditModel;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_generator")
    @SequenceGenerator(name="inventory_generator", sequenceName = "inventory_seq")
    Integer id;

    String image="default-item.jpg";//image filename

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

}
