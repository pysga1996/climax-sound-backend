package com.lambda.event.listener;

import com.lambda.model.entities.Role;
import com.lambda.model.entities.User;
import com.lambda.service.RoleService;
import com.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
@Transactional
public class DataSeedingListener {
    private boolean alreadySetup = false;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void onApplicationEvent() {
        if (alreadySetup)
            return;
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        createAccounts();
        alreadySetup = true;
    }

    private void createRoleIfNotFound(String authority) {

        Role role = roleService.findByAuthority(authority);
        if (role == null) {
            role = new Role(authority);
            roleService.save(role);
        }
    }

    private void createAccounts() {
        String password;
        String firstName;
        String lastName = "Lambda";

        // Member account
        String username = "member";
        HashSet<Role> roles2 = new HashSet<>();
        if (!userService.findByUsername(username).isPresent()) {
            password = passwordEncoder.encode("Lambda123456");
            firstName = "Member";
            roles2.add(roleService.findByAuthority("ROLE_USER"));
            User member = new User(username, password, roles2);
            member.setGender(true);
            member.setFirstName(firstName);
            member.setLastName(lastName);
            userService.save(member, true);
        }

        // Admin account
        username = "admin";

        if (!userService.findByUsername(username).isPresent()) {
            password = passwordEncoder.encode("Lambda123456");
            firstName = "Admin";
            roles2.add(roleService.findByAuthority("ROLE_ADMIN"));
            User admin = new User(username, password, roles2);
            admin.setGender(true);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            userService.save(admin, true);
        }
    }
}
