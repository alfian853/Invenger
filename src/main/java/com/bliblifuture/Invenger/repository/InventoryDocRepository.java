package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.inventory.InventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryDocRepository extends JpaRepository<InventoryDocument,Integer> {

}
