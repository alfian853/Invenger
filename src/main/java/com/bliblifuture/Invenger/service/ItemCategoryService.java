package com.bliblifuture.Invenger.service;


import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.repository.CategoryRepository;
import com.bliblifuture.Invenger.response.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemCategoryService {


    @Autowired
    protected CategoryRepository categoryRepository;

    private List<Category> categories;
    private int getCategoryIndex(int id){
        int lower_bound = 0;
        int upper_bound = categories.size()-1;
        int mid;
        while(lower_bound <= upper_bound){
            mid = (lower_bound + upper_bound) >> 1;
            System.out.println(mid+": "+categories.get(mid).getId());
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
        Category current = categories.get( getCategoryIndex(id) );
        int oldNameLength = current.getName().length();

        current.setName(parentNewName + current.getName().substring(parentOldNameLength));
//        System.out.println(current.getName());

        for(Category child: categoryRepository.findAllByParentId(id)) {
            updateCategoryUtil(child.getId(), current.getName(), oldNameLength);
        }
    }


    public RequestResponse updateCategory(int id, String newName){
        RequestResponse response = new RequestResponse();
       try{
           categories = categoryRepository.findAllOrderById();
        int c_idx = getCategoryIndex(id);
        Category current = categories.get(c_idx);
        int oldNameLength = current.getName().length();
        for(int i = oldNameLength-1; i >= 0; --i){
            if( current.getName().charAt(i) == '/' ){
                current.setName( current.getName().substring(0,i+1) + newName );
                break;
            }
        }
        System.out.println(categories.get(c_idx).getName());


        for(Category child: categoryRepository.findAllByParentId(id)){
            updateCategoryUtil(child.getId(),current.getName(),oldNameLength);
        }

        categoryRepository.saveAll(categories);
        response.setStatusToSuccess();
        return response;
       }
       catch (Exception e){
           response.setStatusToFailed();
           return response;
       }
    }

    public RequestResponse createCategory(int parentId,String name){
        RequestResponse response = new RequestResponse();
        if(name.contains("/")){
            response.setStatusToFailed();
            return response;
        }
        Category parent = categoryRepository.findCategoryById(parentId);
        if(parent == null){
            response.setStatusToFailed();
            return response;
        }
        Category newCategory = new Category();
        newCategory.setParent(parent);
        newCategory.setName(parent.getName()+"/"+name);

        categoryRepository.save(newCategory);
        response.setStatusToSuccess();
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

}
