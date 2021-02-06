package com.alpha.climaxsound.repositories_test;

import com.alpha.model.entity.Country;
import com.alpha.repositories.CountryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    private Country UK;

    @BeforeAll
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
        Country HK = new Country("Hong Kong");
        countryRepository.save(HK);
        Country found = countryRepository.findByName("Hong Kong");
        assertThat(found).isNotNull();
    }

    @Test
    public void whenDelete_thenNotFoundInDB() {
        countryRepository.deleteById(UK.getId());
        Country found = countryRepository.findByName("UK");
        assertThat(found).isNull();
    }
}
