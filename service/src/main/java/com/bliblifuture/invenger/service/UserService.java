package com.bliblifuture.invenger.service;

import com.bliblifuture.invenger.ModelMapper.user.UserMapper;
import com.bliblifuture.invenger.Utils.DataTablesUtils;
import com.bliblifuture.invenger.Utils.MyUtils;
import com.bliblifuture.invenger.Utils.QuerySpec;
import com.bliblifuture.invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.invenger.annotation.imp.PhoneValidator;
import com.bliblifuture.invenger.exception.DataNotFoundException;
import com.bliblifuture.invenger.exception.DefaultException;
import com.bliblifuture.invenger.exception.DuplicateEntryException;
import com.bliblifuture.invenger.exception.InvalidRequestException;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.entity.user.RoleType;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.request.datatables.DataTablesRequest;
import com.bliblifuture.invenger.request.formRequest.UserCreateRequest;
import com.bliblifuture.invenger.request.formRequest.UserEditRequest;
import com.bliblifuture.invenger.request.jsonRequest.ProfileRequest;
import com.bliblifuture.invenger.response.jsonResponse.*;
import com.bliblifuture.invenger.response.jsonResponse.search_response.SearchResponse;
import com.bliblifuture.invenger.response.viewDto.PositionDTO;
import com.bliblifuture.invenger.response.viewDto.ProfileDTO;
import com.bliblifuture.invenger.response.viewDto.UserDTO;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    MyUtils myUtils;

    private DataTablesUtils<User> dataTablesUtils;

    public UserService(){
        dataTablesUtils = new DataTablesUtils<>(mapper);
    }

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public List<UserDTO> getAll(){
        return mapper.toUserDtoList(userRepository.findAllFetched());
    }

    public UserDTO getById(Integer id) throws Exception {
        User user = userRepository.findUserById(id);
        if(user == null){
            throw new DataNotFoundException("User Not Found");
        }
        return mapper.toUserDto(user);
    }

    private void saveUserHandler(User user) throws Exception {
        try{
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException e){
            System.out.println(e.getRootCause().getLocalizedMessage());
            if(e.getRootCause().getLocalizedMessage().contains("duplicate")){
                if(e.getRootCause().getLocalizedMessage().contains("username")){
                    throw new DuplicateEntryException("Username already exist");
                }
                else{
                    throw new DuplicateEntryException("Email already exist");
                }
            }
        }
    }

    public UserCreateResponse createUser(UserCreateRequest request) throws Exception {
        UserCreateResponse response = new UserCreateResponse();
        response.setStatusToSuccess();

        String imgName = myUtils.getRandomFileName(request.getProfile_photo());

        Position newPosition = new Position();
        newPosition.setId(request.getPosition_id());

        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(myUtils.getBcryptHash(request.getPassword()));
        newUser.setTelp(request.getTelp());
        newUser.setPictureName(imgName);
        newUser.setPosition(newPosition);
        newUser.setRole(RoleType.ROLE_USER.toString());
        newUser.setSuperior(userRepository.getOne(request.getSuperior_id()));

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RequestResponse updateUser(UserEditRequest request) throws Exception {

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

    public RequestResponse deleteUser(Integer id) throws Exception {
        RequestResponse response = new RequestResponse();
        try{
            userRepository.deleteById(id);
        }
        catch(Exception e){
            if(e instanceof EmptyResultDataAccessException){
                throw new DataNotFoundException("User Doesn\'t Exists!");
            }
            else{
                throw new DefaultException(e.getLocalizedMessage());
            }
        }
        response.setStatusToSuccess();
        response.setMessage("User Deleted");
        return response;
    }

    public User getSessionUser(){
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            return null;
        }
        else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User){
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    /*
    * load by username or email for login and auto login purpose
    * */
    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(s);
        if(user == null){
            return userRepository.findByEmail(s);
        }
        return user;
    }

    public boolean currentUserIsAdmin(){
        return RoleType.isEqual(RoleType.ROLE_ADMIN, this.getSessionUser().getRole());
    }

    public ProfileDTO getProfile(){
        User user = this.getSessionUser();
        Position position = user.getPosition();
        try {
            position.getName();
        }
        catch (Exception e){
            position = positionRepository.findUserPosition(user.getId());
            user.setPosition(position);
        }
        return ProfileDTO.builder()
                .id(user.getId())
                .name(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .position(user.getPosition().getName())
                .pictureName(user.getPictureName())
                .telp(user.getTelp())
                .build();
    }

    public Map<String,FormFieldResponse> editProfile(ProfileRequest request) throws Exception {
        User user = null;
        Map<String,FormFieldResponse> formResponses = new HashMap<>();
        FormFieldResponse formResponse = null;

        if(!request.getNewTelp().equals("") ){

            formResponse = new FormFieldResponse("new-telp");

            if(PhoneValidator.isValid(request.getNewTelp())){
                user = this.getSessionUser();
                if(!request.getNewTelp().equals(user.getTelp())){
                    user.setTelp(request.getNewTelp());
                    userRepository.save(user);
                    formResponse.setStatusToSuccess();
                    formResponse.setMessage("Phone number updated successfuly");
                }
            }
            else{
                formResponse.setStatusToFailed();
                formResponse.setMessage(PhoneValidator.getErrorMessage());
            }
            formResponses.put("new-telp",formResponse);
        }

        while(!request.getOldPwd().equals("") ){

            String newPassword = request.getNewPwd1();
            formResponse = new FormFieldResponse();

            if(newPassword.equals(request.getOldPwd())){
                break;
            }

            if(request.getNewPwd1().equals("")){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }
            if(request.getNewPwd2().equals("")){
                formResponse.setField_name("new-pwd2");
                formResponse.setStatusToFailed();
                formResponse.setMessage("password field can't be empty");
                formResponses.put("password",formResponse);
                break;
            }

            if(!newPassword.equals(request.getNewPwd2()) ) {
                formResponse.setField_name("new-pwd2");
                formResponse.setStatusToFailed();
                formResponse.setMessage("new password doesn't match");
                formResponses.put("password",formResponse);
                break;
            }

            if(!PasswordValdator.isValid(newPassword)){
                formResponse.setField_name("new-pwd1");
                formResponse.setStatusToFailed();
                formResponse.setMessage("Weak password");
                formResponses.put("password",formResponse);
                break;
            }

            if(user == null){
                user = (User) this.getSessionUser();
            }
            if(myUtils.matches(request.getOldPwd(),user.getPassword() )){
                user.setPassword(myUtils.getBcryptHash(newPassword));
                this.saveUserHandler(user);
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToSuccess();
                formResponse.setMessage("Change password success");

            }
            else{
                formResponse.setField_name("old-pwd");
                formResponse.setStatusToFailed();
                formResponse.setMessage("wrong password");
            }

            formResponses.put("password",formResponse);

            break;
        }
        return formResponses;
    }

    public UploadProfilePictResponse changeProfilePict(MultipartFile file){
        UploadProfilePictResponse response = new UploadProfilePictResponse();
        if(file == null){
            response.setStatusToSuccess();
            return response;
        }
        String fileName = myUtils.getRandomFileName(file);

        if(fileStorageService.storeFile(file,fileName, FileStorageService.PathCategory.PROFILE_PICT) ){
            User user = this.getSessionUser();
            String oldFileName = user.getPictureName();

            boolean oldFileDeleted = false;
            if(oldFileName.equals("default-pict.png")){
                oldFileDeleted = true;
            }
            else{
                oldFileDeleted = fileStorageService.deleteFile(oldFileName,FileStorageService.PathCategory.PROFILE_PICT);
            }

            if(oldFileDeleted){
                user.setPictureName(fileName);
                userRepository.save(user);
                response.setNew_pict_src("/profile/pict/"+fileName);
                response.setStatusToSuccess();

            }
            else {
                fileStorageService.deleteFile(fileName, FileStorageService.PathCategory.PROFILE_PICT);
                response.setStatusToFailed();
            }

        }
        else{
            response.setStatusToFailed();
        }
        return response;
    }
    public SearchResponse getSearchedUser(String query, Integer pageNum, Integer length,Integer minLevel) {

        PageRequest pageRequest = PageRequest.of(pageNum,length);
        Specification<User> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), "%" + query.toLowerCase() + "%")
            );
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), "%" + query.toLowerCase() + "%")
            );
            if(minLevel != null){
                Predicate levelPredicate = criteriaBuilder.greaterThan(root.get("position").get("level"),minLevel);
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

    public List<PositionDTO> getAllPosition(){
        return mapper.toPositionDtoList(positionRepository.findAll());
    }

    private void savePositionHandler(Position position) throws DefaultException {
        try{
            positionRepository.save(position);
        }
        catch (DataIntegrityViolationException e){
            if(e.getRootCause().getLocalizedMessage().contains("duplicate")){
                if(e.getRootCause().getLocalizedMessage().contains("username")){
                    throw new DuplicateEntryException("Position already exist!");
                }
                throw new DefaultException(e.getLocalizedMessage());
            }
        }
    }

    public PositionCreateResponse createPosition(PositionDTO newPosition) throws DefaultException {
        Position position = Position.builder()
                .name(newPosition.getName())
                .level(newPosition.getLevel())
                .build();

        this.savePositionHandler(position);

        PositionCreateResponse response = new PositionCreateResponse();

        if(position != null && position.getId() != null){
            response.setMessage("New position added to table");
            response.setStatusToSuccess();
            response.setPositionId(position.getId());
        }
        else{
            response.setMessage("Unknown error");
            response.setStatusToFailed();
        }

        return response;

    }

    public RequestResponse editPosition(PositionDTO editedPosition) throws DefaultException {

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

    public RequestResponse deletePosition(Integer id) throws DefaultException {
        try{
            positionRepository.deleteById(id);
        }
        catch(Exception e){
            if(e instanceof EmptyResultDataAccessException){
                throw new DataNotFoundException("Position Doesn\'t Exists!");
            }
            else{
                throw new DefaultException(e.getLocalizedMessage());
            }
        }
        RequestResponse response = new RequestResponse();
        response.setStatusToSuccess();
        response.setMessage("Position deleted");

        return response;
    }

    public DataTablesResult<UserDataTableResponse> getPaginatedDatatablesUserList(DataTablesRequest request){
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

        result.setListOfDataObjects(mapper.toUserDatatables(page.getContent()));
        result.setDraw(Integer.parseInt(request.getDraw()));
        result.setRecordsFiltered((int) page.getTotalElements());
        result.setRecordsTotal((int) this.userRepository.count());
        return result;
    }

}
