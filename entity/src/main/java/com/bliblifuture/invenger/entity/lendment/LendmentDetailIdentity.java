package com.bliblifuture.invenger.entity.lendment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LendmentDetailIdentity implements Serializable {

    @Column(name = "lendment_id")
    protected Integer lendmentId;

    @Column(name = "inventory_id")
    protected Integer inventoryId;

}
