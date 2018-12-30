package com.bliblifuture.invenger.controller;

import com.bliblifuture.invenger.ModelMapper.user.UserMapper;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import com.bliblifuture.invenger.service.AccountService;
import com.bliblifuture.invenger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private MockMvc mvc;

    @Mock
    UserService userService;

    @Mock
    AccountService accountService;

    @InjectMocks
    UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new DefaultControllerAdvice())
                .build();
    }

      ///////////////////////////////////////////////
     //public String getUserTablePage(Model model)//
    ///////////////////////////////////////////////

    @Test
    public void getUserTablePage_test() throws Exception {
        List<UserDTO> users = new ArrayList<>();
        users.add(UserDTO.builder().id(1).build());
        when(userService.getAll()).thenReturn(users);

        MockHttpServletResponse response = mvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user_list"))
                .andExpect(model().attribute("users",users)).andReturn().getResponse();

    }

      //////////////////////////////////////////////////////////////////////////////////////////
     //public UserCreateResponse addNewUser(@Valid @ModelAttribute UserCreateRequest request)//
    //////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void addNewUser_test() throws Exception {
        MockMultipartFile pic = new MockMultipartFile
                ("data", "filename.png",
                        "image/png", "".getBytes()
                );
        Position position = Position.builder().id(1).build();
        User superUser = User.builder().id(1).build();
        User user = User.builder()
                .id(2)
                .superior(superUser)
                .position(position)
                .pictureName(pic.getName())
                .fullName("name")
                .username("user")
                .password("pass")
                .telp("12345678910")
                .build();

        System.out.println(user);

        UserCreateResponse response = new UserCreateResponse();
        response.setUser_id(user.getId());
        response.setStatusToSuccess();

        when(userService.createUser(any())).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.multipart("/user/create")
                .file("profile_photo", pic.getBytes())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username","user")
                .param("password", "pass")
                .param("telp","12345678910")
                .param("superior_id","1")
                .param("fullName","name")
                .param("position_id","1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

    }

      ///////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse editUser(@Valid @ModelAttribute UserEditRequest request)//
    ///////////////////////////////////////////////////////////////////////////////////

    @Test
    public void editUser_test() throws Exception {
        Position position = Position.builder().id(1).build();
        User superUser = User.builder().id(1).build();
        UserEditRequest request = new UserEditRequest();
        request.setId(2);
        request.setSuperior_id(superUser.getId());
        request.setPosition_id(position.getId());
        request.setPassword("pass");

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(userService.updateUser(request)).thenReturn(response);

        mvc.perform(post("/user/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "2")
                .param("password", "pass")
                .param("superior_id","1")
                .param("position_id","1")
        )
                .andExpect(status().isOk());

    }

      /////////////////////////////////////////////////////////////////////
     //public RequestResponse removeUser(@PathVariable("id") Integer id)//
    /////////////////////////////////////////////////////////////////////

    @Test
    public void removeUser_test() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(userService.deleteUser(anyInt())).thenReturn(response);

        mvc.perform(post("/user/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService,times(1)).deleteUser(anyInt());
    }

      ////////////////////////////////////////////////////////////////////////////
     //public String getUserDetail(Model model, @PathVariable("id") Integer id)//
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void getUserDetail_test() throws Exception {
        Position position = Position.builder().id(1).name("position").build();
        User superUser = User.builder().id(1).fullName("super").build();
        UserDTO userDTO = UserDTO.builder()
                .id(1)
                .superior(superUser.getFullName())
                .position(position.getName())
                .fullName("name")
                .username("user")
                .telp("12345678910")
                .build();

        System.out.println(userDTO);

        when(userService.getById(1)).thenReturn(userDTO);

        mvc.perform(get("/user/detail/{id}", 1)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("user/user_detail"));

        verify(userService, times(1)).getById(1);
    }

      ///////////////////////////////////////////////
     //public String getPositionTable(Model model)//
    ///////////////////////////////////////////////

    @Test
    public void getPositionTable_test() throws Exception {
        List<PositionDTO> positions = new ArrayList<>();
        positions.add(PositionDTO.builder().id(1).name("position").build());

        when(userService.getAllPosition()).thenReturn(positions);

        MockHttpServletResponse response = mvc.perform(get("/user/positions"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/position_list"))
                .andExpect(model().attribute("positions",positions)).andReturn().getResponse();
    }

      /////////////////////////////////////////////////////////////////////////////////////////////
     //public PositionCreateResponse createPosition(@Valid @RequestBody PositionDTO positionDTO)//
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void createPosition_test() throws Exception {
        Position position = Position.builder().id(1).name("position").build();

        HashMap<String, Object> content = new HashMap<>();
        content.put("name", "name");
        content.put("level", 1);

        PositionCreateResponse response = new PositionCreateResponse();
        response.setPositionId(position.getId());
        response.setStatusToSuccess();

        when(userService.createPosition(any())).thenReturn(response);

        mvc.perform(post("/user/positions/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(content))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value(true));
        verify(userService,times(1)).createPosition(any());
    }

      ////////////////////////////////////////////////////////////////////////////////
     //public RequestResponse editPosition(@RequestBody PositionDTO editedPosition)//
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void editPosition_success() throws Exception {
        PositionDTO positionDTO = PositionDTO.builder()
                .id(1)
                .name("position")
                .level(1)
                .build();

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(userService.editPosition(any())).thenReturn(response);

        mvc.perform(post("/user/positions/edit")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(positionDTO))
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value(true));
        verify(userService,times(1)).editPosition(any());
    }

    @Test
    public void editPosition_invalidRequest() throws Exception {
        PositionDTO positionDTO = PositionDTO.builder()
                .id(null)
                .name("position")
                .level(1)
                .build();

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        mvc.perform(post("/user/positions/edit")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(positionDTO))
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value(false));
        verify(userService,times(0)).editPosition(any());
    }

      /////////////////////////////////////////////////////////////////////////
     //public RequestResponse deletePosition(@PathVariable("id") Integer id)//
    /////////////////////////////////////////////////////////////////////////

    @Test
    public void deletePosition_test() throws Exception {
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();

        when(userService.deletePosition(anyInt())).thenReturn(response);

        mvc.perform(post("/user/positions/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService,times(1)).deletePosition(anyInt());
    }

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //public SearchResponse searchUser(@RequestParam("search")String query, @RequestParam("page")Integer page, @RequestParam("length")Integer length, @RequestParam(value = "min_level",required = false) Integer minLevel)//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void searchUser_test() throws Exception {
        SearchResponse response = new SearchResponse();
        response.setRecordsFiltered(4000);
        response.setResults(new LinkedList<>());

        when(userService.getSearchResult(any())).thenReturn(response);

        mvc.perform(get("/user/search")
                .param("search","all")
                .param("page","2")
                .param("min_level", "1")
                .param("length","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").value(response.getResults()))
                .andExpect(jsonPath("$.recordsFiltered").value(response.getRecordsFiltered()));

        verify(userService,times(1)).getSearchResult(any());
    }


      /////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //public DataTablesResult<UserDataTableResponse> getPaginatedInventories(HttpServletRequest servletRequest)//
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MockHttpServletRequest mock_datatableServletRequest(boolean hasSearchValue) {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addParameter("columns[0][data]", "id");
        servletRequest.addParameter("columns[0][name]", "id");
        servletRequest.addParameter("columns[0][orderable]", "true");
        servletRequest.addParameter("columns[0][search][regex]", "false");
        servletRequest.addParameter("columns[0][search][value]", (hasSearchValue) ? "123" : "");
        servletRequest.addParameter("columns[0][searchable]", "true");
        servletRequest.addParameter("draw", "1");
        servletRequest.addParameter("length", "10");
        servletRequest.addParameter("order[0][column]", "0");
        servletRequest.addParameter("order[0][dir]", "asc");
        servletRequest.addParameter("search[regex]", "false");
        servletRequest.addParameter("search[value]", "");
        servletRequest.addParameter("start", "0");
        return servletRequest;
    }

    @Test
    public void getPaginatedInventories_test() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = this.mock_datatableServletRequest(true);
        DataTablesRequest request = new DataTablesRequest(mockHttpServletRequest);

        UserMapper mapper = Mappers.getMapper(UserMapper.class);

        Page<User> page = new PageImpl<>(new ArrayList<>());

        DataTablesResult<UserDataTableResponse> dataTablesResult = new DataTablesResult<>();
        dataTablesResult.setListOfDataObjects(mapper.toDataTablesDtoList(page.getContent()));
        dataTablesResult.setDraw(Integer.parseInt(request.getDraw()));
        dataTablesResult.setRecordsFiltered(page.getNumberOfElements());
        dataTablesResult.setRecordsTotal((int) page.getTotalElements());

        mvc.perform(get("/user/datatables")
        )
                .andExpect(status().isOk());
    }


}