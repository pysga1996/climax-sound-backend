package com.lambda.climaxsound.repositories_test;

import com.lambda.model.entities.Artist;
import com.lambda.repositories.ArtistRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArtistRepositoryTest {
    @Autowired
    ArtistRepository artistRepository;

    private Pageable pageable = PageRequest.of(0,10);

    @Test
    public void whenFindAll_returnArtistIterable() {
        Page<Artist> found = artistRepository.findAll(pageable);
        assertThat(found.getTotalElements()).isEqualTo(9);
    }

//    @Test
//    public void whenFindByUnaccentName_returnArtistIterable() {
//        Iterable<Artist> found = artistRepository.findAllByUnaccentNameContainingIgnoreCase("lam");
//        Iterator iterator = found.iterator();
//        int resultCount = 0;
//        while (iterator.hasNext()) {
//            resultCount++;
//        }
//        assertThat(resultCount).isEqualTo(1);
//    }

}
