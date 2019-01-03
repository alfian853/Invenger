package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.ModelMapper.category.CategoryMapper;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.CategoryWithChildId;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.SearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.CategoryDTO;
import com.bliblifuture.invenger.service.ItemCategoryService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemCategoryServiceImpl implements ItemCategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    private List<CategoryWithChildId> categories;

    final private CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);


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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoryEditResponse updateCategory(CategoryEditRequest request){

        CategoryEditResponse response = new CategoryEditResponse();

        if(request.getNewParentId().equals(request.getId())){
            throw new InvalidRequestException("Can't assign it self as parent");
        }

        if(request.getNewName().contains("/")){
            throw new InvalidRequestException("category name can't be contain '/' character");
        }

        categories = categoryRepository.getCategoryParentWithChildIdOrderById();
        int currentIndex = getCategoryIndex(request.getId());
        CategoryWithChildId current = categories.get(currentIndex);

        //if edit base category
        if(current.getName().equals("/all")){
            throw new InvalidRequestException("Can't edit /all category");
        }

        int oldNameLength = current.getName().length();

        boolean nameChanged = !current.getName().substring(current.getName().length()-request.getNewName().length())
                .equals(request.getNewName());

        boolean parentChanged = !current.getParentId().equals(request.getNewParentId());

        // check is value changed?
        if( !nameChanged && !parentChanged){
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
                throw new InvalidRequestException("can\'t assign child as new parent");
            }
        }


        Category currentCtg = categoryRepository.getOne(current.getId());

        if(nameChanged){//parent id not changed
            for(int i = oldNameLength-1; i >= 0; --i){
                if( current.getName().charAt(i) == '/' ){
                    current.setName( current.getName().substring(0,i+1) + request.getNewName() );
                    currentCtg.setName(current.getName());
//                    categoryRepository.updateNameById(current.getId(),current.getName());
                    break;
                }
            }
        }

        //parent id changed
        if(parentChanged){
            current.setParentId(parent.getId());
            current.setName(parent.getName()+"/"+request.getNewName());
            currentCtg.setName(current.getName());
            currentCtg.setParent(categoryRepository.getOne(current.getParentId()));
        }

        categoryRepository.save(currentCtg);

        for(Integer childId: current.getChildsId()){
            updateCategoryUtil(childId,current.getName(),oldNameLength);
        }

        for(CategoryWithChildId element : categories){
            response.addCategoryData(element.getId(),element.getName(), element.getParentId());
        }
        response.setStatusToSuccess();
        return response;

    }

    @Override
    public CategoryCreateResponse createCategory(CategoryCreateRequest request){
        CategoryCreateResponse response = new CategoryCreateResponse();
        if(request.getName().contains("/") || request.getName().length() == 0){
            throw new InvalidRequestException("Name can\'t be contain \'/\'");
        }
        Category parent = categoryRepository.findCategoryById(request.getParentId());
        if(parent == null){
            throw new InvalidRequestException("Parent ID can\'t be null");
        }
        Category newCategory = new Category();
        newCategory.setParent(parent);
        newCategory.setName(parent.getName()+"/"+request.getName());
        categoryRepository.save(newCategory);
        response.setStatusToSuccess();
        response.setCategory(newCategory);
        return response;
    }

    @Override
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

    @Override
    public List<CategoryDTO> getAllItemCategory(boolean fetchParent){
        if(fetchParent){
            return mapper.toDtoList(categoryRepository.findAllFetchParent());
        }
        else{
            return mapper.toDtoList(categoryRepository.findAll());
        }
    }

    @Override
    public SearchResponse getSearchResult(SearchRequest request){
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getLength());
        Specification<Category> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), "%" + request.getQuery().toLowerCase() + "%")
            );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Category> page = categoryRepository.findAll(specification, pageRequest);
        SearchResponse response = new SearchResponse();
        response.setResults(mapper.toSearchResultList(page.getContent()));
        response.setRecordsFiltered((int) page.getTotalElements());

        return response;
    }
}
