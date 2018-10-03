package com.bliblifuture.Invenger.service;


import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.repository.category.CategoryWithChildId;
import com.bliblifuture.Invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.CategorCreateyResponse;
import com.bliblifuture.Invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCategoryService {


    @Autowired
    protected CategoryRepository categoryRepository;

    private List<CategoryWithChildId> categories;

    // Get category index in categories by category id using Binary Search algorithm
    private int getCategoryIndex(int id){
        int lower_bound = 0;
        int upper_bound = categories.size()-1;
        int mid;
        while(lower_bound <= upper_bound){
            mid = (lower_bound + upper_bound) >> 1;
            if(categories.get(mid).getId() < id){
                lower_bound = mid + 1;
            }
            else if(categories.get(mid).getId() > id){
                upper_bound = mid - 1;
            }
            else{
                return mid;
            }
        }
        return -1;
    }

    private void updateCategoryUtil(int id,String parentNewName,int parentOldNameLength){
        int currentIndex = getCategoryIndex(id);
        CategoryWithChildId current = categories.get(currentIndex);
        int oldNameLength = current.getName().length();

        current.setName(parentNewName + current.getName().substring(parentOldNameLength));
        categoryRepository.updateNameById(current.getId(),current.getName());

        for(Integer childId: current.getChildsId()) {
            updateCategoryUtil(childId, current.getName(), oldNameLength);
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoryEditResponse updateCategory(CategoryEditRequest request){

        categories = categoryRepository.getCategoryParentWithChildIdOrderById();

        int currentIndex = getCategoryIndex(request.getId());
        CategoryWithChildId current = categories.get(currentIndex);
        int oldNameLength = current.getName().length();
        for(int i = oldNameLength-1; i >= 0; --i){
            if( current.getName().charAt(i) == '/' ){
                current.setName( current.getName().substring(0,i+1) + request.getNewName() );
                categoryRepository.updateNameById(current.getId(),current.getName());
                break;
            }
        }

        for(Integer childId: current.getChildsId()){
            updateCategoryUtil(childId,current.getName(),oldNameLength);
        }

        CategoryEditResponse response = new CategoryEditResponse();

        for(CategoryWithChildId element : categories){
            response.addCategoryData(element.getId(),element.getName(),element.getParentId());
        }

        return response;

    }

    public CategorCreateyResponse createCategory(CategoryCreateRequest request){
        CategorCreateyResponse response = new CategorCreateyResponse();
        if(request.getName().contains("/") || request.getName().length() == 0){
            response.setStatusToFailed();
            return response;
        }
        Category parent = categoryRepository.findCategoryById(request.getParentId());
        if(parent == null){
            response.setStatusToFailed();
            return response;
        }
        Category newCategory = new Category();
        newCategory.setParent(parent);
        newCategory.setName(parent.getName()+"/"+request.getName());

        categoryRepository.save(newCategory);
        response.setStatusToSuccess();
        response.setCategory(newCategory);
        return response;
    }

    public RequestResponse deleteCategory(int id){
        RequestResponse response = new RequestResponse();
        if(categoryRepository.existsByParentId(id)){
            response.setStatusToFailed();
            response.setMessage("Can't delete a record while other records still reference it");
            return response;
        }
        else{
            categoryRepository.deleteById(id);
            response.setStatusToSuccess();
            return response;
        }
    }

    public List<Category> getAllItemCategory(){
        return categoryRepository.findAll();
    }

}
