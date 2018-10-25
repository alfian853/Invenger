package com.bliblifuture.Invenger.repository.category;

import com.bliblifuture.Invenger.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedNativeQuery;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>, CategoryRepositoryExtension {

    @Query("select c from Category c order by c.id")
    List<Category> findAllOrderById();

    Category findCategoryById(Integer id);

    @Query("select c from Category c where c.parent.id = :parent_id")
    List<Category> findAllByParentId(@Param("parent_id") Integer id);

    @Modifying
    @Query("update Category c set c.name = :new_name where c.id = :id")
    void updateNameById(@Param("id") Integer id,@Param("new_name") String newName);

    boolean existsByParentId(Integer parentId);

    Category findCategoryByName(String name);

}

