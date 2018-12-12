package com.bliblifuture.invenger.repository.category;

import com.bliblifuture.invenger.entity.inventory.CategoryWithChildId;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class CategoryRepositoryExtensionImpl implements CategoryRepositoryExtension {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<CategoryWithChildId> getCategoryParentWithChildIdOrderById() {
        Query q = em.createNativeQuery(
                "select c.id,c.name,c.parent_id,array_agg(x.id) as childsId" +
                        " from categories c left join categories x on c.id = x.parent_id " +
                        "group by c.id order by c.id","MyMapping");
        return q.getResultList();
    }


}
