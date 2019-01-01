package com.bliblifuture.invenger.service;


import com.bliblifuture.invenger.ModelMapper.user.UserMapper;
import com.bliblifuture.invenger.ModelMapper.user.UserMapperImpl;
import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.DefaultRuntimeException;
import com.bliblifuture.invenger.exception.DuplicateEntryException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.UserSearchRequest;
import com.bliblifuture.invenger.response.jsonResponse.RequestResponse;
import com.bliblifuture.invenger.response.jsonResponse.UserCreateResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import com.bliblifuture.invenger.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    private final UserMapper mapper = new UserMapperImpl();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MyUtils myUtils;

    private static Position POSITION = Position.builder()
            .id(1)
            .level(2)
            .name("admin")
            .build();

    private static Integer ID = 14;
    private static String FULL_NAME = "full dummy";
    private static String USERNAME = "dummy";
    private static String EMAIL = "dummy@dumail.com";
    private static String PASSWORD = new BCryptPasswordEncoder().encode("password");
    private static String TELP = "12345678910";

    private static User USER = User.builder()
            .id(ID)
            .email(EMAIL)
            .fullName(FULL_NAME)
            .username(USERNAME)
            .password(PASSWORD)
            .telp(TELP)
            .position(POSITION)
            .build();

    @Before
    public void init(){
        when(myUtils.getRandomFileName(any(MultipartFile.class))).thenReturn("abcdefg");
    }



      ////////////////////////////////////////////////////////////////////
     //public SearchResponse getSearchResult(UserSearchRequest request)//
    ////////////////////////////////////////////////////////////////////

    @Test
    public void getSearchResult_test() {
        Page<User> page = new PageImpl<>(new ArrayList<>());

        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        UserSearchRequest request = new UserSearchRequest();
        request.setQuery("query");
        request.setLength(10);
        request.setPageNum(1);
        request.setMinLevel(1);

        userService.getSearchResult(request);

        verify(userRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }

      ///////////////////////////////////////////////////////////////////////////////////////////////
     //public DataTablesResult<UserDataTableResponse> getDatatablesData(DataTablesRequest request)//
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
    public void getDatatablesData_onlySortByColumn() {
        DataTablesRequest request = new DataTablesRequest(this.mock_datatableServletRequest(false));
        Page<User> page = new PageImpl<>(new ArrayList<>());

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

        userService.getDatatablesData(request);

        verify(userRepository, times(1)).findAll(any(PageRequest.class));

    }

    @Test
    public void getDatatablesData_sortAndSearchByColumn() {
        MockHttpServletRequest servletRequest = this.mock_datatableServletRequest(true);
        DataTablesRequest request = new DataTablesRequest(servletRequest);

        Page<User> page = new PageImpl<>(new ArrayList<>());
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        userService.getDatatablesData(request);

        verify(userRepository, times(1))
                .findAll(any(Specification.class), any(PageRequest.class));

    }

      /////////////////////////////////
     //public List<UserDTO> getAll()//
    /////////////////////////////////

    @Test
    public void getAll_test(){
        Position position = Position.builder().id(1).build();
        LinkedList<User> users = new LinkedList<>();
        users.push(User.builder()
                .id(1)
                .username("user")
                .fullName("name")
                .password("pass")
                .position(position)
                .telp("12345678910")
                .build());

        when(userRepository.findAllFetched()).thenReturn(users);

        Assert.assertEquals(userService.getAll(),mapper.toDtoList(users));
    }

      //////////////////////////////////////
     //public UserDTO getById(Integer id)//
    //////////////////////////////////////

    @Test
    public void getById_success() {
        Position position = Position.builder().id(1).build();
        User user = User.builder()
                .id(1)
                .username("user")
                .fullName("name")
                .password("pass")
                .position(position)
                .telp("12345678910")
                .build();
        UserDTO userDTO = mapper.toDto(user);
        when(userRepository.findUserById(ID)).thenReturn(user);
        Assert.assertEquals(userDTO, userService.getById(ID));
    }

    @Test(expected = DataNotFoundException.class)
    public void getById_idNotFound() {
        when(userRepository.findUserById(ID)).thenReturn(null);
        userService.getById(ID);
    }

      /////////////////////////////////////////////////
     //public RequestResponse deleteUser(Integer id)//
    /////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void deleteUser_idNotFound() {
        when(userService.deleteUser(1)).thenThrow(EmptyResultDataAccessException.class);
        userService.deleteUser(1);
    }

    @Test(expected = DefaultRuntimeException.class)
    public void deleteUser_runtimeException() {
        when(userService.deleteUser(1)).thenThrow(RuntimeException.class);
        userService.deleteUser(1);
    }

    @Test
    public void deleteUser_success() {
        RequestResponse response = userService.deleteUser(1);
        Assert.assertTrue(response.isSuccess());
    }

      ///////////////////////////////////////////////////////////////////
     //public UserCreateResponse createUser(UserCreateRequest request)//
    ///////////////////////////////////////////////////////////////////


    private UserCreateRequest mock_userCreateRequest(boolean validRequest){

        when(userRepository.findUserById(10)).thenReturn(USER);

        UserCreateRequest request = new UserCreateRequest();
        request.setFullName(FULL_NAME);
        request.setUsername(USERNAME);
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);
        request.setTelp(TELP);
        request.setProfile_photo(new MockMultipartFile("file", "orig", null, "user".getBytes()));
        request.setSuperior_id(10);

        Position lowPosition = Position.builder().id(2).level(1).build();
        if(validRequest){
            when(positionRepository.findPositionById(lowPosition.getId()))
                    .thenReturn(lowPosition);
            request.setPosition_id(lowPosition.getId());
        }
        else{
            when(positionRepository.findPositionById(POSITION.getId()))
                    .thenReturn(POSITION);
            request.setPosition_id(POSITION.getId());
        }

        return request;
    }

    @Test(expected = InvalidRequestException.class)
    public void createUser_invalidRequest(){
        userService.createUser(this.mock_userCreateRequest(false));
    }

    @Test(expected = DuplicateEntryException.class)
    public void createUser_duplicateEntry(){
        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("duplicate username"));
        when(storageService.storeFile(any(), any(), any())).thenReturn(true);
        userService.createUser(this.mock_userCreateRequest(true));
    }

    @Test
    public void createUser_fileStored() {

        when(storageService.storeFile(any(), any(), any())).thenReturn(true);

        UserCreateResponse response = new UserCreateResponse();
        response.setStatusToSuccess();

        Assert.assertEquals(userService.createUser(this.mock_userCreateRequest(true)), response);

        verify(storageService, times(1)).storeFile(any(), any(), any());

    }

    @Test
    public void createUser_fileNotStored() {
        when(storageService.storeFile(any(), any(), any())).thenReturn(false);

        UserCreateResponse response = new UserCreateResponse();
        response.setStatusToFailed();

        Assert.assertEquals(
                userService.createUser(this.mock_userCreateRequest(true)),
                response
        );

        verify(storageService, times(1)).storeFile(any(), any(), any());

    }

      //////////////////////////////////////////////////////////////
     //public RequestResponse updateUser(UserEditRequest request)//
    //////////////////////////////////////////////////////////////

    @Test
    public void updateUser_success() {
        Position position = Position.builder().id(100).level(4).build();
        User superUser = User.builder().id(10).position(position).build();

        when(userRepository.findUserById(anyInt())).thenReturn(USER);

        UserEditRequest request = new UserEditRequest();
        request.setId(USER.getId());
        request.setPassword("passwords");
        request.setPosition_id(USER.getPosition().getId());
        request.setSuperior_id(10);

        when(positionRepository.getOne(anyInt())).thenReturn(USER.getPosition());

        when(userRepository.findUserById(request.getSuperior_id())).thenReturn(superUser);

        RequestResponse response = userService.updateUser(request);

        Assert.assertTrue(response.isSuccess());

    }

    @Test(expected = InvalidRequestException.class)
    public void updateUser_invalidRequest() {
        Position position = Position.builder().id(100).level(1).build();
        User superUser = User.builder().id(10).position(position).build();

        when(userRepository.findUserById(anyInt())).thenReturn(USER);

        UserEditRequest request = new UserEditRequest();
        request.setId(USER.getId());
        request.setPassword("passwords");
        request.setPosition_id(USER.getPosition().getId());
        request.setSuperior_id(10);

        when(positionRepository.getOne(anyInt())).thenReturn(USER.getPosition());

        when(userRepository.findUserById(request.getSuperior_id())).thenReturn(superUser);

        RequestResponse response = userService.updateUser(request);

        Assert.assertTrue(response.isSuccess());

    }

      /////////////////////////////////////////////
     //public List<PositionDTO> getAllPosition()//
    /////////////////////////////////////////////

    @Test
    public void getAllPosition_test() {
        LinkedList<Position> positions = new LinkedList<>();
        positions.push(Position.builder()
                .id(1)
                .name("pos")
                .level(1)
                .build());

        when(positionRepository.findAll()).thenReturn(positions);

        Assert.assertEquals(userService.getAllPosition(),mapper.toPositionDtoList(positions));

    }

      /////////////////////////////////////////////////////////////////////////
     //public PositionCreateResponse createPosition(PositionDTO newPosition)//
    /////////////////////////////////////////////////////////////////////////

    @Test(expected = DuplicateEntryException.class)
    public void createPosition_duplicateEntry() {
        PositionDTO positionDTO = PositionDTO.builder()
                .name("p")
                .level(1)
                .build();

        when(positionRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate name"));
        userService.createPosition(positionDTO);
    }

    @Test
    public void createPosition_success() {
        PositionDTO positionDTO = PositionDTO.builder()
                .name("p")
                .level(1)
                .build();

        Assert.assertTrue(userService.createPosition(positionDTO).isSuccess());
    }



      ///////////////////////////////////////////////////////////////////
     //public RequestResponse editPosition(PositionDTO editedPosition)//
    ///////////////////////////////////////////////////////////////////

    @Test
    public void editPosition_success() {
        Position position = Position.builder()
                .id(33)
                .name("pos")
                .level(1)
                .build();

        when(positionRepository.getOne(anyInt())).thenReturn(position);

        PositionDTO positionDTO = PositionDTO.builder()
                .id(33)
                .name("p")
                .level(1)
                .build();

        RequestResponse response = userService.editPosition(positionDTO);

        Assert.assertTrue(response.isSuccess());

    }

    @Test(expected = DataNotFoundException.class)
    public void editPosition_notFound() {
        when(positionRepository.getOne(anyInt())).thenReturn(null);

        PositionDTO positionDTO = PositionDTO.builder()
                .id(-1)
                .name("p")
                .level(1)
                .build();

        RequestResponse response = userService.editPosition(positionDTO);

        Assert.assertTrue(response.isSuccess());

    }

      /////////////////////////////////////////////////////
     //public RequestResponse deletePosition(Integer id)//
    /////////////////////////////////////////////////////

    @Test(expected = DataNotFoundException.class)
    public void deletePosition_idNotFound() {
        when(userService.deletePosition(1)).thenThrow(EmptyResultDataAccessException.class);
        userService.deletePosition(1);
    }

    @Test(expected = DefaultRuntimeException.class)
    public void deletePosition_runtimeException() {
        when(userService.deletePosition(1)).thenThrow(RuntimeException.class);
        userService.deletePosition(1);
    }

    @Test
    public void deletePosition_success() {
        RequestResponse response = userService.deletePosition(1);
        Assert.assertTrue(response.isSuccess());
    }



}