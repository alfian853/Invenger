package com.bliblifuture.invenger.repository.category;

import com.bliblifuture.invenger.model.inventory.CategoryWithChildId;

import java.util.List;

public interface CategoryRepositoryExtension {
    List<CategoryWithChildId> getCategoryParentWithChildIdOrderById();
}