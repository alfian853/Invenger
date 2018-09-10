package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Position;
import javafx.geometry.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position,Integer> {
    Position findByName(String name);
}
