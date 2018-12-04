package com.bliblifuture.invenger.model.lendment;


import com.bliblifuture.invenger.model.AuditModel;
import com.bliblifuture.invenger.model.user.User;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
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
