package com.bliblifuture.invenger.service.impl;

import com.bliblifuture.invenger.ModelMapper.user.UserMapper;
import com.bliblifuture.invenger.Utils.DataTablesUtils;
import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.Utils.QuerySpec;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.RoleType;
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
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import com.bliblifuture.invenger.service.FileStorageService;
import com.bliblifuture.invenger.service.UserService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    MyUtils myUtils;

    private DataTablesUtils<User> dataTablesUtils;

    public UserServiceImpl(){
        dataTablesUtils = new DataTablesUtils<>(mapper);
    }

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Override
    public List<UserDTO> getAll(){
        return mapper.toDtoList(userRepository.findAllFetched());
    }

    @Override
    public UserDTO getById(Integer id) {
        User user = userRepository.findUserById(id);
        if(user == null){
            throw new DataNotFoundException("User Not Found");
        }
        return mapper.toDto(user);
    }

    private void saveUserHandler(User user) {
        System.out.println(user);
        try{
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException e){
            if(e.getLocalizedMessage().contains("duplicate")){
                if(e.getLocalizedMessage().contains("username")){
                    throw new DuplicateEntryException("Username already exist");
                }
                else{
                    throw new DuplicateEntryException("Email already exist");
                }
            }
        }
    }

    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        UserCreateResponse response = new UserCreateResponse();
        response.setStatusToSuccess();

        User superior = userRepository.findUserById(request.getSuperior_id());
        Position position = positionRepository.findPositionById(request.getPosition_id());

        if(superior.getPosition().getLevel() <= position.getLevel()){
            throw new InvalidRequestException("can't assign inferior as superior");
        }

        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(myUtils.getBcryptHash(request.getPassword()));
        newUser.setTelp(request.getTelp());
        newUser.setPosition(position);
        newUser.setRole(RoleType.ROLE_USER.toString());
        newUser.setSuperior(superior);

        String imgName = myUtils.getRandomFileName(request.getProfile_photo());
        newUser.setPictureName(imgName);

        if(request.getProfile_photo() != null){
            if(!fileStorageService.storeFile(
                    request.getProfile_photo(),
                    imgName,
                    FileStorageService.PathCategory.PROFILE_PICT)
            ) {
                response.setStatusToFailed();
            }
        }

        if(response.isSuccess()){
            this.saveUserHandler(newUser);
            response.setUser_id(newUser.getId());
        }
        response.setMessage("User Added to System");
        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse updateUser(UserEditRequest request) {

        User user = userRepository.findUserById(request.getId());

        if(request.getPassword() != null){
            user.setPassword(myUtils.getBcryptHash(request.getPassword()));
        }

        if(request.getPosition_id() != null){
            Position position = positionRepository.getOne(request.getPosition_id());
            user.setPosition(position);
        }

        if(request.getSuperior_id() != null){
            User superior = userRepository.findUserById(request.getSuperior_id());
            if(superior.getPosition().getLevel() <= user.getPosition().getLevel()){
                throw new InvalidRequestException("Invalid Request Superior level");
            }
            user.setSuperior(superior);
        }

        this.saveUserHandler(user);

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        return response;
    }

    @Override
    public RequestResponse deleteUser(Integer id) {
        RequestResponse response = new RequestResponse();
        try{
            userRepository.deleteById(id);
        }
        catch(Exception e){
            if(e instanceof EmptyResultDataAccessException){
                throw new DataNotFoundException("User Doesn\'t Exists!");
            }
            else{
                throw new DefaultRuntimeException(e.getLocalizedMessage());
            }
        }
        response.setStatusToSuccess();
        response.setMessage("User Deleted");
        return response;
    }

    @Override
    public SearchResponse getSearchResult(UserSearchRequest request) {

        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getLength());
        Specification<User> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), "%" +
                            request.getQuery().toLowerCase() + "%")
            );
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), "%" +
                            request.getQuery().toLowerCase() + "%")
            );
            if(request.getMinLevel() != null){
                Predicate levelPredicate = criteriaBuilder.greaterThan(root.get("position").get("level"),
                        request.getMinLevel()
                );
                return criteriaBuilder.and(criteriaBuilder.or(predicates.toArray(new Predicate[0])),levelPredicate);
            }
            else{
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
        };
        Page<User> page = userRepository.findAll(specification,pageRequest);
        SearchResponse response = new SearchResponse();
        response.setResults(mapper.toSearchResultList(page.getContent()));
        response.setRecordsFiltered((int) page.getTotalElements());

        return response;
    }

    @Override
    public List<PositionDTO> getAllPosition(){ return mapper.toPositionDtoList(positionRepository.findAll()); }

    private void savePositionHandler(Position position) { try{ positionRepository.save(position); }
        catch (DataIntegrityViolationException e){
            if(e.getLocalizedMessage().contains("duplicate")){
                if(e.getLocalizedMessage().contains("name")){
                    throw new DuplicateEntryException("Position already exist!");
                }
                throw new DefaultRuntimeException(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public PositionCreateResponse createPosition(PositionDTO newPosition) {
        Position position = Position.builder()
                .name(newPosition.getName())
                .level(newPosition.getLevel())
                .build();

        this.savePositionHandler(position);

        PositionCreateResponse response = new PositionCreateResponse();

        response.setMessage("New position added to table");
        response.setStatusToSuccess();
        response.setPositionId(position.getId());

        return response;
    }

    @Override
    public RequestResponse editPosition(PositionDTO editedPosition) {

        Position position = positionRepository.getOne(editedPosition.getId());
        if(position == null){
            throw new DataNotFoundException("Can\'t find \"position\" with id ="+editedPosition.getId());
        }
        if(editedPosition.getLevel() != null){
            position.setLevel(editedPosition.getLevel());
        }
        if(editedPosition.getName() != null){
            position.setName(editedPosition.getName());
        }
        this.savePositionHandler(position);

        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        return response;
    }

    @Override
    public RequestResponse deletePosition(Integer id) {
        try{
            positionRepository.deleteById(id);
        }
        catch(Exception e){
            if(e instanceof EmptyResultDataAccessException){
                throw new DataNotFoundException("Position Doesn\'t Exists!");
            }
            else{
                throw new DefaultRuntimeException(e.getLocalizedMessage());
            }
        }
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        response.setMessage("Position deleted");

        return response;
    }

    @Override
    public DataTablesResult<UserDataTableResponse> getDatatablesData(DataTablesRequest request){
        QuerySpec<User> spec = dataTablesUtils.getQuerySpec(request);

        Page<User> page;

        DataTablesResult<UserDataTableResponse> result = new DataTablesResult<>();

        if(spec.getSpecification() == null){
            System.out.println("no filter");
            page = userRepository.findAll(spec.getPageRequest());
        }
        else{
            System.out.println("has filter");
            page = userRepository.findAll(spec.getSpecification(),spec.getPageRequest());
        }

        result.setListOfDataObjects(mapper.toDataTablesDtoList(page.getContent()));
        result.setDraw(Integer.parseInt(request.getDraw()));
        result.setRecordsFiltered((int) page.getTotalElements());
        result.setRecordsTotal(result.getRecordsFiltered());
        return result;
    }

}
