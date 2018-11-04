package com.bliblifuture.Invenger.model.lendment;

import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@EqualsAndHashCode
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "lendments_detail")
public class LendmentDetail implements Serializable {

    @EmbeddedId
    LendmentDetailIdentity cmpId;

    Integer quantity;

    boolean isReturned = false;

    @Nullable
    @Column(name = "return_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    Date returnDate;

    @JsonIgnore
    @MapsId("lendment_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lendment_id",referencedColumnName = "id",nullable = false)
    Lendment lendment;

    @JsonIgnore
    @MapsId("inventory_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    Inventory inventory;

}
