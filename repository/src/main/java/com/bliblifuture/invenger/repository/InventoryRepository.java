package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.model.inventory.Inventory;
import com.bliblifuture.invenger.model.inventory.InventoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Integer>,JpaSpecificationExecutor<Inventory> {

    Inventory findInventoryById(Integer id);

    @Query("select i from Inventory i join fetch i.category")
    List<Inventory> findAllFetchCategory();
}
