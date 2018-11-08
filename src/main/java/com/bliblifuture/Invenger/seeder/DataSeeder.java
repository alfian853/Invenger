package com.bliblifuture.Invenger.seeder;

import com.bliblifuture.Invenger.model.Category;
import com.bliblifuture.Invenger.model.Position;
import com.bliblifuture.Invenger.model.user.User;
import com.bliblifuture.Invenger.model.user.RoleType;
import com.bliblifuture.Invenger.repository.PositionRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import com.bliblifuture.Invenger.repository.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @EventListener
    public void initSeeder(ContextRefreshedEvent event){
        postionSeeder();
        adminSeeder();
        userSeeder();

        categorySeeder();

    }

    void categorySeeder(){

        Category category = categoryRepository.findCategoryByName("/all");

        if(category == null){
            category = Category.builder()
                    .name("/all")
                    .level(0)
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

    void adminSeeder(){
        User admin = userRepository.findByUsername("root");
        if(admin == null){
            admin = new User();
            admin.setUsername("root");
            admin.setFullName("root");
            admin.setEmail("root@future.com");
            admin.setTelp("+123456789");
            admin.setRole(RoleType.ROLE_ADMIN.toString());
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
            user.setFullName("biasa saja");
            user.setEmail("basics@future.com");
            user.setTelp("+1111111111");
            user.setRole(RoleType.ROLE_USER.toString());
            user.setPassword(new BCryptPasswordEncoder().encode("basic"));
            user.setPosition(positionRepository.findByName("junior software engineer"));
            userRepository.save(user);
        }
    }

}
