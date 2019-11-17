package com.lambda.climaxsound;

import com.lambda.model.entity.Country;
import com.lambda.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class CountryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void findByName() {
        // given
        Country china = new Country("China");
        entityManager.persist(china);
        entityManager.flush();

        // when
        Country found = countryRepository.findByName(china.getName());

        // then
        assertThat(found.getName())
                .isEqualTo(china.getName());
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllByNameContaining() {
    }
}