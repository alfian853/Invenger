package com.bliblifuture.invenger.service;
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
import com.bliblifuture.invenger.service.impl.ItemCategoryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class itemCategoryServiceTest {

    @InjectMocks
    private ItemCategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private static int PARENT_ID = 1;


    @Before
    public void initAttribute(){
    }

      ///////////////////////////////////////////////////////////////////////////
     //public CategoryEditResponse updateCategory(CategoryEditRequest request)//
    ///////////////////////////////////////////////////////////////////////////

    private String CATEGORY_NEWNAME = "dua";
    private int CATEGORY_ID = 2;


    private List<CategoryWithChildId> mock_getCategoryParentWithChildOrderById(){

        List<CategoryWithChildId> categories = new LinkedList<>();
        categories.add(CategoryWithChildId.builder()
                .id(1).name("/all")
                .childsId(Arrays.asList(2,3))
                .parentId(null).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(2).name("/all/two")
                .childsId(Arrays.asList(4))
                .parentId(1).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(3).name("/all/three")
                .childsId(Arrays.asList())
                .parentId(1).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(4).name("/all/two/four")
                .childsId(Arrays.asList())
                .parentId(2).build());

        return categories;
    }

    private CategoryEditResponse mock_updateCategory_parentNotChanged_result(){

        CategoryEditResponse response = new CategoryEditResponse();
        response.addCategoryData(1,"/all",null);
        response.addCategoryData(2,"/all/"+CATEGORY_NEWNAME,1);
        response.addCategoryData(3,"/all/three",1);
        response.addCategoryData(4,"/all/"+CATEGORY_NEWNAME+"/four",2);
        response.setStatusToSuccess();
        return response;
    }

    private CategoryEditResponse mock_updateCategory_parentChanged_result(){

        CategoryEditResponse response = new CategoryEditResponse();
        response.addCategoryData(1,"/all",null);
        response.addCategoryData(2,"/all/three/"+CATEGORY_NEWNAME,3);
        response.addCategoryData(3,"/all/three",1);
        response.addCategoryData(4,"/all/three/"+CATEGORY_NEWNAME+"/four",2);
        response.setStatusToSuccess();
        return response;
    }

    @Test(expected = InvalidRequestException.class)
    public void updateCategory_invalidNewName(){
        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(CATEGORY_ID);
        request.setNewName("tidak/valid");
        request.setNewParentId(100);
        categoryService.updateCategory(request);
    }

    @Test(expected = InvalidRequestException.class)
    public void updateCategory_updateRoot(){
        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(1);
        request.setNewParentId(2);
        request.setNewName("yeah");

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        categoryService.updateCategory(request);
    }

    @Test(expected = InvalidRequestException.class)
    public void updateCategory_assignSelfAsParent(){
        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(1);
        request.setNewParentId(1);
        categoryService.updateCategory(request);
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void updateCategory_idNotFound(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(-1);
        request.setNewName(CATEGORY_NEWNAME);
        request.setNewParentId(2);
        categoryService.updateCategory(request);
    }

    @Test
    public void updateCategory_dataNotChanged(){
        List<CategoryWithChildId> categories = this.mock_getCategoryParentWithChildOrderById();

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(categories);

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(categories.get(1).getId());
        request.setNewName("two");
        request.setNewParentId(categories.get(1).getParentId());
        RequestResponse response = categoryService.updateCategory(request);

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getMessage(),"no data changed");
    }

    @Test
    public void updateCategory_parentNotChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        when(categoryRepository.getOne(2)).thenReturn(new Category());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(CATEGORY_ID);
        request.setNewName(CATEGORY_NEWNAME);
        request.setNewParentId(1);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response, mock_updateCategory_parentNotChanged_result() );
    }

    @Test
    public void updateCategory_parentChanged(){
        when(categoryRepository.getOne(2)).thenReturn(new Category());

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(CATEGORY_ID);
        request.setNewName(CATEGORY_NEWNAME);
        request.setNewParentId(3);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response, mock_updateCategory_parentChanged_result() );

    }

    @Test
    public void updateCategory_validParentChanged(){

        when(categoryRepository.getOne(2)).thenReturn(new Category());

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(CATEGORY_ID);
        request.setNewName("two");
        request.setNewParentId(3);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertTrue(response.isSuccess());
    }

    @Test(expected = InvalidRequestException.class)
    public void updateCategory_notValidParentChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(CATEGORY_ID);
        request.setNewName("two");
        request.setNewParentId(4);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertFalse(response.isSuccess());
    }




      ///////////////////////////////////////////////////////////////////////////////
     //public CategoryCreateResponse createCategory(CategoryCreateRequest request)//
    ///////////////////////////////////////////////////////////////////////////////

    @Test(expected = InvalidRequestException.class)
    public void createCategory_invalidRequest(){
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("chi/ld");
        request.setParentId(PARENT_ID);
        categoryService.createCategory(request);
    }

    @Test
    public void createCategory_parentFound(){

        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("child");
        request.setParentId(PARENT_ID);

        Category parent = Category.builder().id(PARENT_ID).name("all/parent").build();
        when(categoryRepository.findCategoryById(PARENT_ID)).thenReturn(parent);

        CategoryCreateResponse response = categoryService.createCategory(request);

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getCategory().getName(),parent.getName()+"/"+request.getName());
    }

    @Test(expected = InvalidRequestException.class)
    public void createCategory_parentNotFound(){
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("child");
        request.setParentId(PARENT_ID);

        when(categoryRepository.findCategoryById(PARENT_ID)).thenReturn(null);
        categoryService.createCategory(request);
    }


      /////////////////////////////////////////////////
     //public RequestResponse deleteCategory(int id)//
    /////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void deleteCategory_idNotFound() {
        when(categoryService.deleteCategory(2000)).thenThrow(EmptyResultDataAccessException.class);
        categoryService.deleteCategory(2000);
    }

    @Test(expected = InvalidRequestException.class)
    public void deleteCategory_isExistAsParent() {

        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(true);
        categoryService.deleteCategory(PARENT_ID);
    }

    @Test
    public void deleteCategory_notExistAsParent() {

        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(false);

        RequestResponse response = categoryService.deleteCategory(PARENT_ID);

        Assert.assertTrue(response.isSuccess());
    }





     ////////////////////////////////////////////////////////////////////
    //public List<CategoryDTO> getAllItemCategory(boolean fetchParent)//
   ////////////////////////////////////////////////////////////////////

    private List<Category> mock_findAll(boolean fetchParent){
        List<Category> categories = new ArrayList<>();
        categories.add(Category.builder()//[0]
                .id(1).name("one")
                .build()
        );
        categories.add(Category.builder()//[1]
                .id(2).name("one/two")
                .build()
        );
        categories.add(Category.builder()//[2]
                .id(3).name("one/three")
                .build()
        );
        categories.add(Category.builder()//[3]
                .id(4).name("one/two/four")
                .build());

        if(fetchParent){
            categories.get(1).setParent(categories.get(0));
            categories.get(2).setParent(categories.get(1));
            categories.get(3).setParent(categories.get(2));
        }

        return categories;
    }


    @Test
    public void getAllItemCategory_fetchParent(){

        List<Category> categories = mock_findAll(true);
        when(categoryRepository.findAllFetchParent()).thenReturn(categories);

        List<CategoryDTO> result = categoryService.getAllItemCategory(true);
        int len = result.size();
        for(int i=0; i<len ; i++){
            Category category = categories.get(i);
            CategoryDTO res = result.get(i);
            Assert.assertEquals(category.getName(),res.getName());
            Assert.assertEquals(category.getId(),res.getId());

            if(category.getParent() == null){
                Assert.assertNull(res.getParent_id());
            }
            else{
                Assert.assertEquals(category.getParent().getId(), res.getParent_id());
            }
        }

    }

    @Test
    public void getAllItemCategory_notFetchParent(){

        List<Category> categories = mock_findAll(false);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> result = categoryService.getAllItemCategory(false);
        int len = result.size();
        for(int i=0; i<len ; i++){
            Category category = categories.get(i);
            Assert.assertEquals(category.getName(),result.get(i).getName());
            Assert.assertEquals(category.getId(),result.get(i).getId());
            Assert.assertNull(result.get(i).getParent_id());
        }

    }

      ////////////////////////////////////////////////////////////////
     //public SearchResponse getSearchResult(SearchRequest request)//
    ////////////////////////////////////////////////////////////////

    @Test
    public void getSearchResult_test(){
        Page page = new PageImpl<>(this.mock_findAll(false));
        System.out.println(page.getContent());

        when(categoryRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(page);

        SearchResponse response = categoryService.getSearchResult(SearchRequest.builder()
                .query("test").length(10).pageNum(1).build()
        );

        verify(categoryRepository,times(1))
                .findAll(any(Specification.class), any(PageRequest.class));






    }


}
