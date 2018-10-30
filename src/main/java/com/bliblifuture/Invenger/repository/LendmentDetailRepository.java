package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.lendment.LendmentDetail;
import com.bliblifuture.Invenger.model.lendment.LendmentDetailIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LendmentDetailRepository extends JpaRepository<LendmentDetail, LendmentDetailIdentity> {

    @Query("select l from LendmentDetail l where l.cmpId.lendmentId = :lendment_id")
    List<LendmentDetail> findAllByLendmentId(@Param("lendment_id") Integer id);

}
