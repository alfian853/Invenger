package com.bliblifuture.Invenger.repository;

import com.bliblifuture.Invenger.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    @Query("select c from Category c order by c.id")
    List<Category> findAllOrderById();
    Category findCategoryById(Integer id);
    @Query("select c from Category c where c.parent.id = :parent_id")
    List<Category> findAllByParentId(@Param("parent_id") Integer id);

    boolean existsByParentId(Integer parentId);

}
