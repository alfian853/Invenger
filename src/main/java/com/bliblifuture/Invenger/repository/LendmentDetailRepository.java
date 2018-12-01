package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.model.lendment.LendmentDetailIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendmentDetailRepository extends JpaRepository<LendmentDetail, LendmentDetailIdentity> {

}
