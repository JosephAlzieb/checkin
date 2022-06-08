package de.checkin.persistenz;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.checkin.domain.klausur.Klausur;
import de.checkin.persistenz.dao.KlausurRepoDao;
import de.checkin.persistenz.impl.KlausurRepoImpl;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:/db/migration/V1__init.sql", "classpath:/db/migration/V2__beispiel_daten.sql"})
public class KlausurRepoImplTest {

  @Autowired
  KlausurRepoDao klausurRepoDao;

  KlausurRepoImpl klausurRepo;

  @BeforeEach
  void initial() {
    klausurRepo = new KlausurRepoImpl(klausurRepoDao);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Klausur wird aus der DB hergeholt")
  void test_1() {
    Klausur klausur = klausurRepo.klausurMitId(1L);

    assertThat(klausur).isNotNull();
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("alle Klausuren werden aus der DB hergeholt")
  void test_2() {
    List<Klausur> klausuren = klausurRepo.findAll();

    assertThat(klausuren.size()).isEqualTo(2);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Eine Klausur wird in der DB gespeichert")
  void test_3() {
    LocalDate tag = LocalDate.of(2022, 3, 25);
    LocalTime von = LocalTime.of(12, 30);
    LocalTime bis = LocalTime.of(13, 30);
    Klausur klausur = new Klausur(3L, "Ana", 125879,
        true, tag, von, bis);

    klausurRepo.save(klausur);
    List<Klausur> klausuren = klausurRepo.findAll();

    assertThat(klausuren.size()).isEqualTo(3);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Klausuren werden mit ihrer Ids aus der DB hergeholt")
  void test_4() {
    List<Klausur> klausuren = klausurRepo.getKlausuren(Set.of(1L, 2L));

    assertThat(klausuren.size()).isEqualTo(2);
  }

}
