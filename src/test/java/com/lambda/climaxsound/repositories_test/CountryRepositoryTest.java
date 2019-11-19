package com.lambda.climaxsound.repositories_test;

import com.lambda.models.entities.Country;
import com.lambda.repositories.CountryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    private Country UK;

    @Before
    public void SetUp() {
        UK = new Country("UK");
        countryRepository.save(UK);
    }

    @Test
    public void whenFindAll_thenReturn4Countries() {
        List<Country> countryList = countryRepository.findAll();
        assertThat(countryList.size()).isEqualTo(5);
    }

    @Test
    public void whenFindById_thenReturnCountry() {
        Optional<Country> found = countryRepository.findById(UK.getId());
        assertThat(found.isPresent()).isTrue();
    }

    @Test
    public void whenFindByName_thenReturnCountry() {
        Country UK = countryRepository.findByName("UK");
        assertThat(UK).isNotNull();
    }

    @Test
    public void whenSave_thenFoundInDB() {
        Country US = new Country("US");
        countryRepository.save(US);
        Country found = countryRepository.findByName("US");
        assertThat(found).isNotNull();
    }

    @Test
    public void whenDelete_thenNotFoundInDB() {
        countryRepository.deleteById(UK.getId());
        Country found = countryRepository.findByName("UK");
        assertThat(found).isNull();
    }
}
