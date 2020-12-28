package com.lambda.climaxsound.repositories_test;

import com.lambda.model.entities.User;
import com.lambda.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnCountry() {
        Optional<User> member = userRepository.findByUsername("member");
        assertThat(member.isPresent()).isTrue();
    }
}
