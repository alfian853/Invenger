package com.bliblifuture.invenger;

import com.bliblifuture.invenger.entity.inventory.Category;
import com.bliblifuture.invenger.entity.user.Position;
import com.bliblifuture.invenger.entity.user.User;
import com.bliblifuture.invenger.entity.user.RoleType;
import com.bliblifuture.invenger.repository.PositionRepository;
import com.bliblifuture.invenger.repository.UserRepository;
import com.bliblifuture.invenger.repository.category.CategoryRepository;
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
            position.setLevel(100);
            position.setName("inventory system admin");
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

}
