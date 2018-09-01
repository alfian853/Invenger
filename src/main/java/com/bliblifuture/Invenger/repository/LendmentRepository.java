package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Lendment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LendmentRepository extends JpaRepository<Lendment,Integer> {
}
