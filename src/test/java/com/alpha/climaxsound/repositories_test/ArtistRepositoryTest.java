package com.alpha.climaxsound.repositories_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.alpha.model.entity.Artist;
import com.alpha.repositories.ArtistRepository;
import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArtistRepositoryTest {

    private final Pageable pageable = PageRequest.of(0, 10);
    @Autowired
    ArtistRepository artistRepository;

    @Test
    public void whenFindAll_returnArtistIterable() {
        Page<Artist> found = artistRepository.findAll(pageable);
        assertThat(found.getTotalElements()).isEqualTo(9);
    }

    @Test
    public void whenFindByUnaccentName_returnArtistIterable() {
        Iterable<Artist> found = artistRepository.findAllByUnaccentNameContainingIgnoreCase("lam");
        Iterator<Artist> iterator = found.iterator();
        int resultCount = 0;
        while (iterator.hasNext()) {
            resultCount++;
            iterator.next();
        }
        assertThat(resultCount).isEqualTo(1);
    }

}
