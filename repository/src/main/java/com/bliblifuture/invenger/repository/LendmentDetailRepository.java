package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.entity.lendment.LendmentDetail;
import com.bliblifuture.invenger.entity.lendment.LendmentDetailIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendmentDetailRepository extends JpaRepository<LendmentDetail, LendmentDetailIdentity> {

}
