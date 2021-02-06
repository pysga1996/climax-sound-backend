package com.alpha.climaxsound.repositories_test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
//    @Autowired
//    UserRepository userRepository;
//
//    @Test
//    public void whenFindByName_thenReturnCountry() {
//        Optional<UserDTO> member = userRepository.findByUsername("member");
//        assertThat(member.isPresent()).isTrue();
//    }
}
