package com.bliblifuture.Invenger.service_test;
import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import com.bliblifuture.Invenger.repository.category.CategoryWithChildId;
import com.bliblifuture.Invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.Invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.Invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.Invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.Invenger.service.ItemCategoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;


public class itemCategoryServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ItemCategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private static int PARENT_ID = 1;

    private RequestResponse successResponse = new RequestResponse();
    private RequestResponse failedResponse = new RequestResponse();

    @Before
    public void initAttribute(){
        successResponse.setStatusToSuccess();
        failedResponse.setStatusToFailed();
    }

    @Test
    public void createCategory_parentFound(){
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("child");
        request.setParentId(PARENT_ID);

        Category parent = Category.builder().id(PARENT_ID).name("all/parent").build();
        when(categoryRepository.findCategoryById(PARENT_ID)).thenReturn(parent);

        CategoryCreateResponse response = categoryService.createCategory(request);

        Assert.assertEquals(response.getStatus(),successResponse.getStatus());
        Assert.assertEquals(response.getCategory().getName(),parent.getName()+"/"+request.getName());
    }

    @Test
    public void createCategory_parentNotFound(){
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("child");
        request.setParentId(PARENT_ID);

        when(categoryRepository.findCategoryById(PARENT_ID)).thenReturn(null);

        CategoryCreateResponse response = categoryService.createCategory(request);
        System.out.println(response);
        Assert.assertEquals(response.getStatus(),failedResponse.getStatus());
    }

    @Test
    public void deleteCategory_isExistAsParent(){
        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(true);

        RequestResponse response = categoryService.deleteCategory(PARENT_ID);

        Assert.assertEquals(response.getStatus(),failedResponse.getStatus());
        Assert.assertEquals(response.getMessage(),"Can't delete a record while other records still reference it");
    }

    @Test
    public void deleteCategory_notExistAsParent(){
        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(false);

        RequestResponse response = categoryService.deleteCategory(PARENT_ID);

        Assert.assertEquals(response.getStatus(),successResponse.getStatus());
    }

    private List<CategoryWithChildId> mock_getCategoryParentWithChildOrderById(){
        List<CategoryWithChildId> categories = new LinkedList<>();
        categories.add(CategoryWithChildId.builder()
                .id(1).name("one")
                .childsId(Arrays.asList(2,3))
                .parentId(null).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(2).name("one/two")
                .childsId(Arrays.asList(4))
                .parentId(1).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(3).name("one/three")
                .childsId(Arrays.asList())
                .parentId(1).build()
        );
        categories.add(CategoryWithChildId.builder()
                .id(4).name("one/two/four")
                .childsId(Arrays.asList())
                .parentId(2).build());

        return categories;
    }

    private String CATEGORY_NEWNAME = "dua";

    private CategoryEditResponse mock_updateCategory_parentNotChanged_result(){
        CategoryEditResponse response = new CategoryEditResponse();
        response.addCategoryData(1,"one",null);
        response.addCategoryData(2,"one/"+CATEGORY_NEWNAME,1);
        response.addCategoryData(3,"one/three",1);
        response.addCategoryData(4,"one/"+CATEGORY_NEWNAME+"/four",2);
        response.setStatusToSuccess();
        return response;
    }

    private CategoryEditResponse mock_updateCategory_parentChanged_result(){
        CategoryEditResponse response = new CategoryEditResponse();
        response.addCategoryData(1,"one",null);
        response.addCategoryData(2,"one/three/"+CATEGORY_NEWNAME,3);
        response.addCategoryData(3,"one/three",1);
        response.addCategoryData(4,"one/three/"+CATEGORY_NEWNAME+"/four",2);
        response.setStatusToSuccess();
        return response;
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void updateCategory_idNotFound(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(-1);
        request.setNewName(CATEGORY_NEWNAME);
        categoryService.updateCategory(request);
    }

    @Test
    public void updateCategory_parentNotChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(2);
        request.setNewName(CATEGORY_NEWNAME);
        request.setNewParentId(1);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response, mock_updateCategory_parentNotChanged_result() );
    }

    @Test
    public void updateCategory_parentChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(2);
        request.setNewName(CATEGORY_NEWNAME);
        request.setNewParentId(3);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response, mock_updateCategory_parentChanged_result() );

    }

    @Test
    public void updateCategory_validParentChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(2);
        request.setNewName("two");
        request.setNewParentId(3);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response.getStatus(),"success");
    }

    @Test
    public void updateCategory_notValidParentChanged(){

        when(categoryRepository.getCategoryParentWithChildIdOrderById())
                .thenReturn(mock_getCategoryParentWithChildOrderById());

        CategoryEditRequest request = new CategoryEditRequest();
        request.setId(2);
        request.setNewName("two");
        request.setNewParentId(4);
        CategoryEditResponse response = categoryService.updateCategory(request);
        Assert.assertEquals(response.getStatus(),"failed");
    }




}
