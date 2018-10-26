package com.bliblifuture.Invenger.seeder;

import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.Role;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.RoleRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder {


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @EventListener
    public void initSeeder(ContextRefreshedEvent event){
        postionSeeder();
        roleSeeder();
        adminSeeder();
        userSeeder();

        categorySeeder();

    }

    void categorySeeder(){

        Category category = categoryRepository.findCategoryByName("/all");

        if(category == null){
            category = Category.builder()
                    .name("/all")
                    .build();

            categoryRepository.save(category);
        }

    }

    void postionSeeder(){

        Position position = positionRepository.findByName("inventory system admin");
        if(position == null){
            position = new Position();
            position.setDescription("manage invenger");
            position.setName("inventory system admin");
            positionRepository.save(position);
        }

        position = positionRepository.findByName("junior software engineer");

        if(position == null){
            position = new Position();
            position.setDescription("basic user");
            position.setName("junior software engineer");
            positionRepository.save(position);
        }

    }

    void roleSeeder(){
        List<Role> roleList = roleRepository.findAll();

        if(roleList.isEmpty()){
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
            role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }
    }

    void adminSeeder(){
        User admin = userRepository.findByUsername("root");
        if(admin == null){
            admin = new User();
            admin.setUsername("root");
            admin.setEmail("root@future.com");
            admin.setTelp("+123456789");
            admin.setRole(roleRepository.findByName("ROLE_ADMIN"));
            admin.setPassword(new BCryptPasswordEncoder().encode("root"));
            admin.setPosition(positionRepository.findByName("inventory system admin"));
            userRepository.save(admin);
        }
    }

    void userSeeder(){
        User user = userRepository.findByUsername("basic");
        if(user == null){
            user = new User();
            user.setUsername("basic");
            user.setEmail("basics@future.com");
            user.setTelp("+1111111111");
            user.setRole(roleRepository.findByName("ROLE_USER"));
            user.setPassword(new BCryptPasswordEncoder().encode("basic"));
            user.setPosition(positionRepository.findByName("junior software engineer"));
            userRepository.save(user);
        }
    }

}
