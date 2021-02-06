package com.alpha.event;

import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class DataSeedingListener {

    private final boolean alreadySetup = false;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeedingListener(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void onApplicationEvent() {
//        if (alreadySetup)
//            return;
//        createRoleIfNotFound("ROLE_ADMIN");
//        createRoleIfNotFound("ROLE_USER");
//        createAccounts();
//        alreadySetup = true;
    }

//    private void createRoleIfNotFound(String authority) {
//
//        RoleDTO role = roleService.findByAuthority(authority);
//        if (role == null) {
//            role = new RoleDTO(authority);
//            roleService.save(role);
//        }
//    }
//
//    private void createAccounts() {
//        String password;
//        String firstName;
//        String lastName = "Lambda";
//
//        // Member account
//        String username = "member";
//        HashSet<RoleDTO> roles2 = new HashSet<>();
//        if (!userService.findByUsername(username).isPresent()) {
//            password = passwordEncoder.encode("Lambda123456");
//            firstName = "Member";
//            roles2.add(roleService.findByAuthority("ROLE_USER"));
//            UserDTO member = new UserDTO(username, password, roles2);
//            member.setGender(true);
//            member.setFirstName(firstName);
//            member.setLastName(lastName);
//            userService.save(member, true);
//        }
//
//        // Admin account
//        username = "admin";
//
//        if (!userService.findByUsername(username).isPresent()) {
//            password = passwordEncoder.encode("Lambda123456");
//            firstName = "Admin";
//            roles2.add(roleService.findByAuthority("ROLE_ADMIN"));
//            UserDTO admin = new UserDTO(username, password, roles2);
//            admin.setGender(true);
//            admin.setFirstName(firstName);
//            admin.setLastName(lastName);
//            userService.save(admin, true);
//        }
//    }
}
