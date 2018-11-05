package com.bliblifuture.Invenger.model.lendment;


import com.bliblifuture.Invenger.model.AuditModel;
import com.bliblifuture.Invenger.model.user.User;
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
    List<LendmentDetail> lendment_details;

    //based on enum: LendmentStatus
    @Column(nullable = false)
    String status;

}
