package com.bliblifuture.Invenger.repository;


import com.bliblifuture.Invenger.model.inventory.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Integer> {
    Inventory findInventoryById(Integer id);
}
