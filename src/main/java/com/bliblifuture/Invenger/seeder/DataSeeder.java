package com.bliblifuture.Invenger.seeder;

import com.bliblifuture.Invenger.model.Role;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.RoleRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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

    @EventListener
    public void initSeeder(ContextRefreshedEvent event){
        roleSeeder();
        adminSeeder();
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
            admin.setRole(roleRepository.findByName("ROLE_ADMIN"));
            admin.setPassword(new BCryptPasswordEncoder().encode("root"));
            userRepository.save(admin);
        }
    }
}
