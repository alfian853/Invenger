package com.bliblifuture.Invenger.model.lendment;


import com.bliblifuture.Invenger.model.AuditModel;
import com.bliblifuture.Invenger.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lendments")
public class Lendment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    
    @OneToMany(mappedBy = "lendment", cascade = CascadeType.ALL)
    List<LendmentDetail> lendment_details;

    //based on enum: LendmentStatus
    String status;

}
