package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.entity.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Integer>,JpaSpecificationExecutor<Inventory> {

    Inventory findInventoryById(Integer id);

    @Query("select i from Inventory i join fetch i.category")
    List<Inventory> findAllFetchCategory();
}
