package com.bliblifuture.invenger.repository;

import com.bliblifuture.invenger.entity.user.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position,Integer> {
    Position findByName(String name);
    @Query("select p from Position p where id = (select u.position.id from User u where u.id = :userid)")
    Position findUserPosition(@Param("userid") Integer userId);

    Position findPositionById(Integer id);
}
