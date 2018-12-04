package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.model.inventory.InventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryDocRepository extends JpaRepository<InventoryDocument,Integer> {

}
