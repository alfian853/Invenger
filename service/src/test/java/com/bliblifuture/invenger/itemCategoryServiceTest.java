package com.bliblifuture.invenger;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.inventory.CategoryWithChildId;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
import com.bliblifuture.invenger.request.jsonRequest.CategoryCreateRequest;
import com.bliblifuture.invenger.request.jsonRequest.CategoryEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.CategoryCreateResponse;
import com.bliblifuture.invenger.response.jsonResponse.CategoryEditResponse;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.service.ItemCategoryService;
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


    @Before
    public void initAttribute(){
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

    @Test
    public void createCategory_parentNotFound(){
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("child");
        request.setParentId(PARENT_ID);

        when(categoryRepository.findCategoryById(PARENT_ID)).thenReturn(null);

        CategoryCreateResponse response = categoryService.createCategory(request);
        System.out.println(response);
        Assert.assertFalse(response.isSuccess());
    }

    @Test(expected = DataNotFoundException.class)
    public void deleteCategory_idNotFound() throws Exception {
//        Not finish yet, need to mock deleteById()
//        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(false);
//        categoryService.deleteCategory(PARENT_ID);
    }

    @Test(expected = InvalidRequestException.class)
    public void deleteCategory_isExistAsParent() throws Exception {
        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(true);
        categoryService.deleteCategory(PARENT_ID);
    }

    @Test
    public void deleteCategory_notExistAsParent() throws Exception {
        when(categoryRepository.existsByParentId(PARENT_ID)).thenReturn(false);

        RequestResponse response = categoryService.deleteCategory(PARENT_ID);

        Assert.assertTrue(response.isSuccess());
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
        Assert.assertTrue(response.isSuccess());
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
        Assert.assertFalse(response.isSuccess());
    }




}
