package de.checkin.domain.klausur;

import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class KlausurTest {

  @Test
  @DisplayName("Anmeldung für eine Online-Klausur")
  void test_1() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(3);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        true, tag, von, bis);
    assertThat(klausur.getFreistellungTag()).isEqualTo(START_TAG.plusDays(2));
    assertThat(klausur.getFreiStellungVon()).isEqualTo(von.minusMinutes(30));
    assertThat(klausur.getFreiStellungBis()).isEqualTo(bis);
  }

  @Test
  @DisplayName("Anmeldung für eine Presänz-Klausur")
  void test_2() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusMinutes(15);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.getFreistellungTag()).isEqualTo(START_TAG.plusDays(2));
    assertThat(klausur.getFreiStellungVon()).isEqualTo(START_ZEIT);
    assertThat(klausur.getFreiStellungBis()).isEqualTo(bis.plusHours(2));
  }

  @Test
  @DisplayName("Rechtzeitige Stornierung einer Klausur")
  void test_3() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusMinutes(15);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.isKlausurStornierbar(START_TAG)).isTrue();
  }

  @Test
  @DisplayName("spätere Stornierung einer Klausur ist nicht möglich")
  void test_4() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusMinutes(15);
    LocalTime bis = von.plusHours(1);
    LocalDate now = START_TAG.plusDays(2);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.isKlausurStornierbar(now)).isFalse();
  }

  @Test
  @DisplayName("FreistellungVon für eine Online Klausur wird erst ab Startzeit des Praktikums berechnet")
  void test_5() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        true, tag, von, bis);
    assertThat(klausur.getFreiStellungVon()).isEqualTo(START_ZEIT);
    assertThat(klausur.getFreiStellungBis()).isEqualTo(bis);
  }

  @Test
  @DisplayName("FreistellungVon für einer Präsenz wird erst ab Startzeit des Praktikums berechnet")
  void test_6() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.getFreiStellungVon()).isEqualTo(START_ZEIT);
  }

  @Test
  @DisplayName("FreistellungBis für eine Präsenz Klausur wird nur bis Endzeit des Praktikum berechnet, FreistellungVon ab Startzeit")
  void test_7() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.getFreiStellungBis()).isEqualTo(END_ZEIT);
  }

  @Test
  @DisplayName("FreistellungVon für eine Präsenz Klausur wird richtig berechnet")
  void test_8() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(3);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        false, tag, von, bis);
    assertThat(klausur.getFreiStellungVon()).isEqualTo(von.minusHours(2));
  }

  @Test
  @DisplayName("FreistellungBis einer online Klausur wird immer bis nur Endzeit des Praktikums berechnet")
  void test_9() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(3).plusMinutes(45);
    LocalTime bis = von.plusHours(1).plusMinutes(30);
    Klausur klausur = new Klausur(1L, "Matching", 125879,
        true, tag, von, bis);
    assertThat(klausur.getFreiStellungVon()).isEqualTo(von.minusMinutes(30));
    assertThat(klausur.getFreiStellungBis()).isEqualTo(END_ZEIT);
  }
}