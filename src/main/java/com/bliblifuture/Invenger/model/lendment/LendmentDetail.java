package com.bliblifuture.Invenger.model.lendment;

import com.bliblifuture.Invenger.model.inventory.Inventory;
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
public class LendmentDetail implements Serializable {

    @EmbeddedId
    LendmentDetailIdentity cmpId;

    Integer quantity;

    boolean isReturned = false;

    @Nullable
    @Column(name = "return_date")
    Date returnDate;

    @MapsId("lendment_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lendment_id",referencedColumnName = "id",nullable = false)
    Lendment lendment;

    @MapsId("inventory_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    Inventory inventory;

}
