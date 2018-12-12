package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.entity.inventory.InventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryDocRepository extends JpaRepository<InventoryDocument,Integer> {

    InventoryDocument findInventoryDocumentById(Integer id);
}
