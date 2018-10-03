package com.bliblifuture.Invenger.repository.category;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryRepositoryExtension {
    List<CategoryWithChildId> getCategoryParentWithChildIdOrderById();
}