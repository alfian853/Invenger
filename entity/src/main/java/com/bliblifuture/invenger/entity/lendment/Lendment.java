package com.bliblifuture.invenger.entity.lendment;


import com.bliblifuture.invenger.entity.AuditModel;
import com.bliblifuture.invenger.entity.user.User;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "lendments")
public class Lendment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lendment_generator")
    @SequenceGenerator(name="lendment_generator", sequenceName = "lendment_seq")
    Integer id;

    //for optimize child count() where isReturned = false
    Integer notReturnedCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    
    @OneToMany(mappedBy = "lendment", cascade = CascadeType.ALL)
    List<LendmentDetail> lendmentDetails;

    //based on enum: LendmentStatus
    @Column(nullable = false)
    String status;

}
