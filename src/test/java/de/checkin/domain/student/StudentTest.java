package de.checkin.domain.student;

import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.checkin.domain.LocalDateTimeWrapper;
import de.checkin.domain.Validierung;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudentTest {

  LocalDateTimeWrapper lw = mock(LocalDateTimeWrapper.class);

  @BeforeEach
  void setUp() {
    when(lw.now()).thenReturn(START_TAG.minusDays(1));
  }


  @Test
  @DisplayName("Ein Benutzer hat sich für eine Klausur angemeldet")
  void Test_1() {
    Student student = new Student(1L, "github");
    student.addKlausurId(2L);
    assertThat(student.getKlausurIds()).contains(2L);
  }

  @Test
  @DisplayName("Ein Benutzer kann ein Urlaub buchen")
  void Test_2() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(30);
    LocalTime bis = von.plusMinutes(30);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).contains(urlaub);
  }

  @Test
  @DisplayName("Ein Benutzer kann Urlaub für mehr als 240 Minuten nicht buchen")
  void Test_3() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(30);
    LocalTime bis = von.plusHours(5);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
  }


  @Test
  @DisplayName("Resturlaub wird richtig reduziert")
  void Test_4() {

    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(30);
    LocalTime bis = von.plusMinutes(30);
    student.addUrlaub(tag, von, bis, false);
    assertThat(student.getRestUrlaub()).isEqualTo(210);
  }

  @Test
  @DisplayName("Urlaube an einem Tag sind für länger als 150 Minuten nicht buchbar - Keine angemeldete Klausur am Urlaubstag")
  void Test_5() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(1);
    LocalTime von1 = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bis1 = von1.plusHours(2).plusMinutes(30);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).contains(urlaub);
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
  }

  @Test
  @DisplayName("2 Urlaube mit länger als 90 Minuten Abstand sind buchbar- Keine angemeldete Klausur am Urlaubstag")
  void Test_6() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusHours(3).plusMinutes(30);
    LocalTime bis1 = von1.plusMinutes(30);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).contains(urlaub);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
  }

  @Test
  @DisplayName("2 Urlaube mit weniger als 90 Minuten Abstand sind nicht buchbar - erster Urlaub liegt nach dem 2ten Urlaub und keine angemeldete Klausur")
  void Test_7() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2);
    LocalTime bis = von.plusHours(1);
    LocalTime von1 = START_ZEIT.plusMinutes(30);
    LocalTime bis1 = von1.plusMinutes(30);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).contains(urlaub);
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
  }

  @Test
  @DisplayName("Eine Überschneidung an dem selben Tag wird vom System Korrigiert erster Urlaub 9:30-10:00 und zweiter Urlaub 10:00-10:30 -> 9:30-10:30")
  void Test_8() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusMinutes(30);
    LocalTime bis1 = von1.plusMinutes(30);
    Urlaub urlaub2 = new Urlaub(tag, von, bis1);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).containsExactly(urlaub2);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
  }

  @Test
  @DisplayName("zweiter Urlaub mit der richtigen Startzeit bzw der Endzeit ist nicht buchbar, weil der erste Urlaub die falschen hat ")
  void Test_9() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusHours(3).plusMinutes(30);
    LocalTime bis1 = von1.plusMinutes(30);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).contains(urlaub);
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(210);
  }

  @Test
  @DisplayName("check gleiche Zeitraum für Urlaub")
  void Test_10() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusHours(1);
    LocalTime bis1 = von1.plusMinutes(30);
    student.addUrlaub(tag, von, bis, false);
    student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube().size()).isEqualTo(1);
    assertThat(student.getRestUrlaub()).isEqualTo(210);
  }

  @Test
  @DisplayName("mehr als 2 Urlaube an dem selben Tag sind nicht mehr buchbar wenn an dem Tag keine Klausur gibt")
  void Test_11() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(15);
    LocalTime von1 = START_ZEIT.plusHours(3).plusMinutes(45);
    LocalTime bis1 = von1.plusMinutes(15);
    LocalTime von2 = START_ZEIT.plusHours(1).plusMinutes(45);
    LocalTime bis2 = von2.plusMinutes(15);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    Urlaub urlaub1 = new Urlaub(tag, von1, bis1);
    student.addUrlaub(tag, von, bis, false);
    student.addUrlaub(tag, von1, bis1, false);
    boolean result = student.addUrlaub(tag, von2, bis2, false);
    assertThat(student.getUrlaube()).contains(urlaub, urlaub1);
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(210);
  }

  @Test
  @DisplayName("Ein Urlaub mit nicht viertelstundenformat kann nicht gespeichert werden")
  void Test_12() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1).plusMinutes(23);
    LocalTime bis = START_ZEIT.plusHours(1).plusMinutes(45);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

  @Test
  @DisplayName("Ein Urlaub mit 0 min wird nicht gespeichert")
  void Test_13() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    boolean result = student.addUrlaub(tag, von, von, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

  @Test
  @DisplayName("Ein Urlaub mit Startzeit nach der Endzeit wird nicht gespeichert")
  void Test_14() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = START_ZEIT.plusMinutes(30);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

  @Test
  @DisplayName("Eine Überschneidung an dem selben Tag wird vom System Korrigiert erster Urlaub ist 10:30-11:30 und zweiter Urlaub ist 9:30-12:00 -> 9:30-12:00")
  void Test_15() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    LocalTime von1 = START_ZEIT;
    LocalTime bis1 = von1.plusHours(2).plusMinutes(30);
    Urlaub urlaub2 = new Urlaub(tag, von1, bis1);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis1, false);
    assertThat(student.getUrlaube()).containsExactly(urlaub2);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(90);
  }

  @Test
  @DisplayName("Eine Überschneidung an dem selben Tag wird vom System Korrigiert erster Urlaub ist 9:30-12:00 und zweiter Urlaub ist 10:30-11:30 -> 9:30-12:00")
  void Test_16() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(2).plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusHours(1);
    LocalTime bis1 = von1.plusHours(1);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von1, bis1, false);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).containsExactly(urlaub);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(90);
  }

  @Test
  @DisplayName("Eine Überschneidung an dem selben Tag wird vom System Korrigiert erster Urlaub ist 10:30-12:00 und zweiter Urlaub ist 10:15-11:30 -> 10:15-12:00 ")
  void Test_17() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1).plusMinutes(30);
    LocalTime von1 = START_ZEIT.plusMinutes(45);
    Urlaub urlaub = new Urlaub(tag, von1, bis);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von1, bis, false);
    assertThat(result).isTrue();
    assertThat(student.getUrlaube()).containsExactly(urlaub);
    assertThat(student.getRestUrlaub()).isEqualTo(135);
  }

  @Test
  @DisplayName("Eine Überschneidung an dem selben Tag wird vom System Korrigiert erster Urlaub ist 10:00-12:00 und zweiter Urlaub ist 12:00-13:30 -> 10:00-13:30")
  void Test_18() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(2);
    LocalTime bis1 = END_ZEIT;
    Urlaub urlaub2 = new Urlaub(tag, von, bis1);
    student.addUrlaub(tag, von, bis, false);
    boolean result = student.addUrlaub(tag, von, bis1, false);
    assertThat(student.getUrlaube()).containsExactly(urlaub2);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(30);
  }

  @Test
  @DisplayName("Ein Benutzer kann kein Urlaub mehr als 150 Minuten an einem Tag buchen")
  void Test_19() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(3).plusMinutes(30);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("eine Klausur kann gelöscht werden")
  void Test_20() {
    Student student = new Student(1L, "github", lw);
    student.addKlausurId(1L);
    student.deleteKlausur(1L);
    assertThat(student.getKlausurIds()).hasSize(0);
  }

  @Test
  @DisplayName("ein Urlaub gefiltert nach bestimmten Tag und bestimmten Uhrzeit wird ausgegeben")
  void Test_21() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(15);
    LocalDate tag1 = START_TAG.plusDays(1);
    LocalTime von1 = START_ZEIT.plusHours(3).plusMinutes(30);
    LocalTime bis1 = von1.plusMinutes(30);
    student.addUrlaub(tag, von, bis, false);
    student.addUrlaub(tag1, von1, bis1, false);
    assertThat(student.getUrlaube()).hasSize(2);
    assertThat(student.getUrlaub(START_TAG.plusDays(2),
        START_ZEIT)).isNotNull();
  }


  @Test
  @DisplayName("Urlaub ist in der Vergangenheit nicht buchbar")
  void Test_22() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.minusDays(5);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(30);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Urlaub nach dem Praktikum nicht buchbar")
  void Test_23() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = Validierung.END_TAG.plusDays(1);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(30);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Urlaub vor dem Praktikum nicht buchbar")
  void Test_24() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = Validierung.START_TAG.minusDays(1);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(30);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(student.getUrlaube()).isEmpty();
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Urlaub wird nicht gelöscht wenn er nicht stonierbar ist")
  void Test_25() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(45);
    LocalTime bis = von.plusMinutes(45);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    when(lw.now()).thenReturn(START_TAG.plusDays(2));
    assertThat(student.deleteUrlaub(urlaub)).isFalse();
    assertThat(student.getUrlaube()).hasSize(1);
    assertThat(student.getRestUrlaub()).isEqualTo(195);

  }

  @Test
  @DisplayName("Urlaub wird gelöscht wenn er stonierbar ist")
  void Test_26() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(45);
    LocalTime bis = von.plusMinutes(45);
    Urlaub urlaub = new Urlaub(tag, von, bis);
    student.addUrlaub(tag, von, bis, false);
    assertThat(student.deleteUrlaub(urlaub)).isTrue();
    assertThat(student.getUrlaube()).hasSize(0);
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

  @Test
  @DisplayName("Urlaub wird nicht hinzugefügt wenn er an dem selben Tag ist"
      + "man sollte den Urlaub also ein Tag vorher eintragen")
  void Test_27() {
    Student student = new Student(1L, "github", lw);
    LocalDate tag = START_TAG.minusDays(1);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(45);
    LocalTime bis = von.plusMinutes(45);
    boolean result = student.addUrlaub(tag, von, bis, false);
    assertThat(result).isFalse();
    assertThat(student.getUrlaube()).hasSize(0);
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

}