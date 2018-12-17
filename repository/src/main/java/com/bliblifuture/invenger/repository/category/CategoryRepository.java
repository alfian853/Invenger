package com.bliblifuture.invenger.repository.category;

import com.bliblifuture.invenger.entity.inventory.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>
        , CategoryRepositoryExtension, JpaSpecificationExecutor<Category> {

    @Query("select c from Category c order by c.id")
    List<Category> findAllOrderById();

    Category findCategoryById(Integer id);

    @Query("select c from Category c LEFT JOIN FETCH c.parent")
    List<Category> findAllFetchParent();

    @Modifying(clearAutomatically = true)
    @Query("update Category c set c.name = :new_name where c.id = :id")
    void updateNameById(@Param("id") Integer id,@Param("new_name") String newName);

    boolean existsByParentId(Integer parentId);

    Category findCategoryByName(String name);

}

