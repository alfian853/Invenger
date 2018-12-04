package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.model.lendment.LendmentDetail;
import com.bliblifuture.invenger.model.lendment.LendmentDetailIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendmentDetailRepository extends JpaRepository<LendmentDetail, LendmentDetailIdentity> {

}
