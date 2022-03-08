package de.checkin.model.klausur;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurTest {

    @Test
    @DisplayName("Anmeldung für eine Online-Klausur")
    void test_1() {
        LocalDateTime von = LocalDateTime.of(2022, 03, 12, 12, 30);
        LocalDateTime bis = LocalDateTime.of(2022, 03, 12, 13, 30);
        Klausur klausur = new Klausur(1L,"Matching",125879,
                true, von, bis);
        assertThat(klausur.getVon()).isEqualTo(LocalDateTime.of(2022, 03, 12, 12, 00));
        assertThat(klausur.getBis()).isEqualTo(LocalDateTime.of(2022, 03, 12, 13, 30));
    }

    @Test
    @DisplayName("Anmeldung für eine Presänz-Klausur")
    void test_2() {
        LocalDateTime von = LocalDateTime.of(2022, 03, 12, 9, 15);
        LocalDateTime bis = LocalDateTime.of(2022, 03, 12, 10, 15);
        Klausur klausur = new Klausur(1L,"Matching",125879,
                false, von, bis);
        assertThat(klausur.getVon()).isEqualTo(LocalDateTime.of(2022, 03, 12, 7, 15));
        assertThat(klausur.getBis()).isEqualTo(LocalDateTime.of(2022, 03, 12, 12, 15));
    }



    @Test
    @DisplayName("Rechtzeitige Stornierung einer Klausur")
    void test_3() {
        LocalDateTime von = LocalDateTime.of(2022, 03, 12, 9, 15);
        LocalDateTime bis = LocalDateTime.of(2022, 03, 12, 10, 15);
        LocalDateTime now = LocalDateTime.of(2022, 03, 7, 10, 15);
        Klausur klausur = new Klausur(1L,"Matching",125879,
                false, von, bis);
        assertThat(klausur.istKlausurStornierbar(now)).isTrue();
    }

    @Test
    @DisplayName("spätere Stornierung einer Klausur")
    void test_4() {
        LocalDateTime von = LocalDateTime.of(2022, 03, 12, 9, 15);
        LocalDateTime bis = LocalDateTime.of(2022, 03, 12, 10, 15);
        LocalDateTime now = LocalDateTime.of(2022, 03, 12, 8, 15);
        Klausur klausur = new Klausur(1L,"Matching",125879,
                false, von, bis);
        assertThat(klausur.istKlausurStornierbar(now)).isFalse();
    }
}
