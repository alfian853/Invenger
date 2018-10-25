package com.bliblifuture.Invenger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lendments_detail")
public class LendmentDetails implements Serializable {

    Integer quantity;

    boolean isReturn = false;

    @Nullable
    Date return_date;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lendment_id",referencedColumnName = "id",nullable = false)
    Lendment lendment;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    Inventory inventory;

}
