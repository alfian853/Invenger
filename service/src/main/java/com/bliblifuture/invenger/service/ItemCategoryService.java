package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.ModelMapper.category.CategoryMapper;
import com.bliblifuture.invenger.ModelMapper.category.CategoryMapperImpl;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.entity.inventory.CategoryWithChildId;
import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

    final private CategoryMapper mapper = new CategoryMapperImpl();

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

        CategoryEditResponse response = new CategoryEditResponse();

        if(request.getNewName().contains("/")){
            response.setStatusToFailed();
            response.setMessage("category name can't be contain '/' character");
            return response;
        }


        categories = categoryRepository.getCategoryParentWithChildIdOrderById();
        int currentIndex = getCategoryIndex(request.getId());
        CategoryWithChildId current = categories.get(currentIndex);
        int oldNameLength = current.getName().length();


        // check is value changed?
        if(current.getName().substring(current.getName().length()-request.getNewName().length())
                .equals(request.getNewName()) &&
                current.getParentId().equals(request.getNewParentId()) ){
            response.setStatusToSuccess();
            response.setMessage("no data changed");
            return response;
        }

        // check is parent change request valid?
        CategoryWithChildId parent = categories.get(
                getCategoryIndex( request.getNewParentId() )
        );
        if(!current.getParentId().equals(request.getNewParentId())){
            CategoryWithChildId tmp = CategoryWithChildId.builder()
                    .id(parent.getId())
                    .parentId(parent.getParentId())
                    .build();

            boolean isCircular = false;
            while (true){
                try{
                    tmp = categories.get(
                            getCategoryIndex(tmp.getParentId())
                    );

                    if(tmp.getId().equals(current.getId())){
                        isCircular = true;
                        break;
                    }
                }
                catch (Exception e){
                    break;
                }
            }

            if(isCircular){
                response.setStatusToFailed();
                response.setMessage("can't assign child as new parent");
                return response;
            }
        }


        //parent id changed
        if( request.getNewParentId() != null && !current.getParentId().equals(request.getNewParentId()) ){
            current.setParentId(parent.getId());
            current.setName(parent.getName()+"/"+request.getNewName());
        }
        else{//parent id not changed
            for(int i = oldNameLength-1; i >= 0; --i){
                if( current.getName().charAt(i) == '/' ){
                    current.setName( current.getName().substring(0,i+1) + request.getNewName() );
                    categoryRepository.updateNameById(current.getId(),current.getName());
                    break;
                }
            }
        }


        for(Integer childId: current.getChildsId()){
            updateCategoryUtil(childId,current.getName(),oldNameLength);
        }

        for(CategoryWithChildId element : categories){
            response.addCategoryData(element.getId(),element.getName(), element.getParentId());
        }
        response.setStatusToSuccess();
        return response;

    }

    public CategoryCreateResponse createCategory(CategoryCreateRequest request){
        CategoryCreateResponse response = new CategoryCreateResponse();
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

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public RequestResponse deleteCategory(int id) {
        RequestResponse response = new RequestResponse();
        if(categoryRepository.existsByParentId(id)){
            throw new InvalidRequestException("Can't delete a record while other records still reference it");
        }
        else{
            try{
                categoryRepository.deleteById(id);
            }
            catch(Exception e){
                e.printStackTrace();
                if(e instanceof EmptyResultDataAccessException){
                    throw new DataNotFoundException("Category Doesn\'t Exists!");
                }
            }
        }

        response.setStatusToSuccess();
        response.setMessage("Item Category Deleted");

        return response;
    }

    public List<CategoryDTO> getAllItemCategory(boolean fetchParent){
        if(fetchParent){
            return mapper.toCategoryDtoList(categoryRepository.findAllFetched());
        }
        else{
            return mapper.toCategoryDtoList(categoryRepository.findAll());
        }
    }

}
