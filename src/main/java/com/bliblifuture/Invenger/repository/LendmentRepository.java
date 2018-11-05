package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.lendment.Lendment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LendmentRepository extends JpaRepository<Lendment,Integer> {

    Lendment findLendmentById(Integer id);

    List<Lendment> findAllByUserId(Integer id);

    @Query("select l from Lendment l join fetch l.user x join fetch x.superior y where y.id = :superior_id and l.status = :status")
    List<Lendment> findAllBySuperiorIdAndStatus(@Param("superior_id") Integer superiorId, @Param("status") String status);


}
