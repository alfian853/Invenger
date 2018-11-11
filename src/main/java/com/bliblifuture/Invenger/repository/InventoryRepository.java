package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.inventory.Inventory;
import com.bliblifuture.Invenger.model.inventory.InventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Integer> {

    Inventory findInventoryById(Integer id);

    @Query("select i from Inventory i join fetch i.document where i.id = :id")
    InventoryDocument findInventoryByIdFetchDocument(@Param("id") Integer id);

}
