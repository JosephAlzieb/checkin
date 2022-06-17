package de.checkin.application.services;

import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.application.repositories.KlausurRepository;
import de.checkin.application.repositories.StudentRepository;
import de.checkin.domain.LocalDateTimeWrapper;
import de.checkin.domain.klausur.Klausur;
import de.checkin.domain.student.Student;
import de.checkin.domain.student.Urlaub;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudentServiceTest {

  private StudentRepository studentRepository = mock(StudentRepository.class);
  private KlausurRepository klausurRepository = mock(KlausurRepository.class);
  private AuditLogRepository auditLogRepository = mock(AuditLogRepository.class);

  private StudentService studentService = new StudentService(studentRepository, klausurRepository,
      auditLogRepository);

  private Student student = mock(Student.class);


  LocalDateTimeWrapper lw = mock(LocalDateTimeWrapper.class);

  @BeforeEach
  void setUp() {
    when(lw.now()).thenReturn(START_TAG.minusDays(1));
  }


  @Test
  @DisplayName("Ein Student kann gespeichert werden")
  void test_1() {
    studentService.save(student);
    verify(studentRepository).save(student);
  }

  @Test
  @DisplayName("Ein Student mit Id wird zurückgegeben")
  void test_2() {

    Student student = new Student(2L, "githubname");

    when(studentRepository.studentMitId(2L)).thenReturn(student);
    studentService.studentMitId(2L);
    verify(studentRepository).studentMitId(2L);

  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es keine Klausur gibt")
  void test_3() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusMinutes(45);
    when(klausurRepository.getKlausuren(any())).thenReturn(List.of());
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, von, bis);
    verify(studentRepository).save(student);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(195);

  }

  @Test
  @DisplayName(
      "Urlaub buchen, an dem Tag, wo es eine Klausur gibt, die überschneiden sich aber nicht." +
          " Urlaub:9:30-10:00 , und Klausur: 11:00-12:00. Also Urlaub ist vor der Klausur")
  void test_4() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT;
    LocalTime bisU = vonU.plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    List<Klausur> klausuren = List.of(new Klausur(1L, "Mat", 1, true, tag, vonK, bisK));
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isTrue();
    verify(studentRepository).save(student);
    assertThat(student.getRestUrlaub()).isEqualTo(210);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 11:15-11:45 , und Klausur: 11:00-12:00. Also Urlaub ist innerhalb der Klausur-Zeit")
  void test_6() {
    Student student = new Student(1L, "githubname");
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1).plusMinutes(45);
    LocalTime bisU = vonU.plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    List<Klausur> klausuren = List.of(new Klausur(1L, "Mat", 1, true, tag, vonK, bisK));
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isFalse();
  }

  @Test
  @DisplayName(
      "Urlaub buchen, an dem Tag, wo es eine Klausur gibt, die überschneiden sich aber nicht." +
          " Urlaub: 12:30-13:30 , und Klausur: 10:00-11:00. Also Urlaub ist nach der Klausur")
  void test_5() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(3);
    LocalTime bisU = vonU.plusHours(1);
    LocalTime vonK = START_ZEIT.plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    List<Klausur> klausuren = List.of(new Klausur(1L, "Mat", 1, true, tag, vonK, bisK));
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isTrue();
    verify(studentRepository).save(student);
    assertThat(student.getRestUrlaub()).isEqualTo(180);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 11:00-12:30 , und Klausur: 11:00-12:00. man bekommt Urlaub nur von 12:00-12:30")
  void test_7() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisU = vonU.plusHours(1).plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    verify(studentRepository).save(student);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(210);
    assertThat(student.getUrlaube()).hasSize(1);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 10:00-12:00 , und Klausur: 11:00-12:00. man bekommt Urlaub nur von 10:00-10:30")
  void test_8() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusMinutes(30);
    LocalTime bisU = vonU.plusHours(2);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    verify(studentRepository).save(student);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(210);
    assertThat(student.getUrlaube()).hasSize(1);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 9:30-12:00 , und Klausur: 12:00-13:00. man bekommt Urlaub nur von 9:30-11:30")
  void test_9() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT;
    LocalTime bisU = vonU.plusHours(2).plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(2).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    verify(studentRepository).save(student);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(120);
    assertThat(student.getUrlaube()).hasSize(1);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 10:30-12:00 , und Klausur: 11:00-12:00. man bekommt keinen Urlaub")
  void test_10() {
    Student student = new Student(1L, "githubname");
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1);
    LocalTime bisU = vonU.plusHours(1).plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
    assertThat(student.getUrlaube()).hasSize(0);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 10:30-11:00 , und Klausur: 11:00-12:00. man bekommt keinen Urlaub")
  void test_11() {
    Student student = new Student(1L, "githubname");
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1);
    LocalTime bisU = vonU.plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
    assertThat(student.getUrlaube()).hasSize(0);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine präsenz Klausur gibt" +
      " Urlaub: 10:30-11:00 , und Klausur: 11:00-12:00. man bekommt keinen Urlaub")
  void test_12() {
    Student student = new Student(1L, "githubname");
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1);
    LocalTime bisU = vonU.plusMinutes(30);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, false, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
    assertThat(student.getUrlaube()).hasSize(0);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine präsenz Klausur gibt" +
      " Urlaub: 10:30-13:30 , und Klausur: 9:30-10:30. man bekommt Urlaub 12:30-13:30")
  void test_13() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT.plusHours(1);
    LocalTime bisU = vonU.plusHours(3);
    LocalTime vonK = START_ZEIT;
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, false, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
    assertThat(student.getUrlaube()).hasSize(1);
  }

  @Test
  @DisplayName("Urlaub buchen, an dem Tag, wo es eine Klausur gibt" +
      " Urlaub: 9:30-13:30 , und Klausur: 11:00-12:00. man bekommt Urlaub 9:30-10:30 und 12:00-13:30")
  void test_14() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonU = START_ZEIT;
    LocalTime bisU = vonU.plusHours(4);
    LocalTime vonK = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK = vonK.plusHours(1);
    Klausur klausur = new Klausur(1L, "Mat", 1, true, tag, vonK, bisK);
    List<Klausur> klausuren = List.of(klausur);
    when(klausurRepository.getKlausuren(any())).thenReturn(klausuren);
    when(studentRepository.studentMitGithubname(anyString())).thenReturn(Optional.of(student));
    boolean urlaub = studentService.createUrlaub(anyString(), tag, vonU, bisU);
    assertThat(urlaub).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(90);
    assertThat(student.getUrlaube()).hasSize(2);
  }

  @Test
  @DisplayName("Urlaub kann gelöscht werden")
  void test_15() {
    Student student = new Student(1L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusMinutes(30);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    student.addUrlaub(tag, von, bis, false);
    studentService.deleteUrlaub("githubname", tag, von);
    verify(studentRepository).save(student);
    assertThat(student.getUrlaube()).hasSize(0);
    assertThat(student.getRestUrlaub()).isEqualTo(240);
  }

  @Test
  @DisplayName("Klausur kann nicht an demselben Tag angemeldet werden")
  void test_16() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    when(lw.now()).thenReturn(tag);
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds()).hasSize(0);
  }

  @Test
  @DisplayName("vergangene Klausur kann nicht angemeldet werden")
  void test_17() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    when(lw.now()).thenReturn(tag.plusDays(1));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds()).hasSize(0);
  }

  @Test
  @DisplayName("Klausur kann rechtzeitig angemeldet werden")
  void test_18() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository).save(student);
    assertThat(student.getKlausurIds()).hasSize(1);
  }

  @Test
  @DisplayName("Klausur kann gelöscht werden")
  void test_19() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    student.addKlausurId(1L);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    studentService.deleteKlausur("githubname", 1L);
    verify(studentRepository).save(student);
    assertThat(student.getKlausurIds()).hasSize(0);
  }

  @Test
  @DisplayName("vergangene angemeldete Klausur kann nicht mehr gelöscht werden")
  void test_20() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    student.addKlausurId(1L);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    when(lw.now()).thenReturn(tag.plusDays(1));
    studentService.deleteKlausur("githubname", 1L);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds()).hasSize(1);
  }

  @Test
  @DisplayName("angemeldete Klausur kann an dem Tag des Klausurtermins nicht storniert werden")
  void test_21() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    student.addKlausurId(1L);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    when(lw.now()).thenReturn(tag);
    studentService.deleteKlausur("githubname", 1L);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds()).hasSize(1);
  }

  @Test
  @DisplayName("Ohne Githubname kann kein Urlaub gebucht werden")
  void test_22() {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(1);
    boolean urlaub = studentService.createUrlaub(null, tag, von, bis);
    assertThat(urlaub).isFalse();

  }

  @Test
  @DisplayName("Ohne Datum des Tages kann kein Urlaub gebucht werden")
  void test_23() {
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(1);
    boolean urlaub = studentService.createUrlaub("github1", null, von, bis);
    assertThat(urlaub).isFalse();

  }

  @Test
  @DisplayName("Ohne Uhrzeit kann kein Urlaub gebucht werden")
  void test_24() {
    LocalDate tag = START_TAG.plusDays(2);
    boolean urlaub = studentService.createUrlaub("github1", tag, null, null);
    assertThat(urlaub).isFalse();

  }

  @Test
  @DisplayName("Eine online Klausur mit FraistellungVon glecih der Endzeit des Praktikums ist nicht anzumelden")
  void test_25() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = END_ZEIT.plusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine online Klausur mit FraistellungBis gleich der Startzeit des Praktikums ist nicht anzumelden")
  void test_26() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusHours(1);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine online Klausur mit FraistellungBis gleich der Endzeit des Praktikums ist anzumelden")
  void test_27() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2).plusMinutes(30);
    LocalTime bis = von.plusHours(1).plusMinutes(30);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(1);

  }

  @Test
  @DisplayName("Eine online Klausur mit FraistellungBis vor der Startzeit des Praktikums ist nicht anzumelden")
  void test_28() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusHours(2);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, true, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine praesenz Klausur mit FraistellungVon glecih der Endzeit des Praktikums ist nicht anzumelden")
  void test_29() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = END_ZEIT.plusHours(2);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, false, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine praesenz Klausur mit FraistellungBis gleich der Startzeit des Praktikums ist nicht anzumelden")
  void test_30() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusHours(3);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, false, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine praesenz Klausur mit FreistellungVon gleich der Startzeit und FraistellungBis gleich der Endzeit des Praktikums ist anzumelden")
  void test_31() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, false, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(1);

  }

  @Test
  @DisplayName("Eine praesenz Klausur mit FraistellungBis vor der Startzeit des Praktikums ist nicht anzumelden")
  void test_32() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.minusHours(3).minusMinutes(30);
    LocalTime bis = von.plusHours(1);
    Klausur klausur = new Klausur(1L, "Matching", 12345, false, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository, never()).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(0);

  }

  @Test
  @DisplayName("Eine praesenz Klausur mit von gleich der Startzeit und FraistellungBis gleich der Endzeit des Praktikums ist anzumelden")
  void test_33() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT;
    LocalTime bis = von.plusHours(2).plusMinutes(30);
    Klausur klausur = new Klausur(1L, "Matching", 12345, false, tag, von, bis);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur);
    verify(studentRepository).save(student);
    assertThat(student.getKlausurIds().size()).isEqualTo(1);

  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt."
      + "K1 11:00-11:30, F1 10:30-11:30, K2 12:30-13:00, F2 12:00-13:00, U 9:30-13:30 "
      + "--> erwartet: 9:30-10:30 und 11:30-12:00 und 13-13:30")
  void test_34() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3);
    LocalTime bisK2 = vonK2.plusMinutes(30);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    Urlaub urlaub1 = new Urlaub(tag, START_ZEIT, START_ZEIT.plusHours(1));
    Urlaub urlaub2 = new Urlaub(tag, START_ZEIT.plusHours(2), START_ZEIT.plusMinutes(150));
    Urlaub urlaub3 = new Urlaub(tag, START_ZEIT.plusMinutes(210), START_ZEIT.plusHours(4));
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag, START_ZEIT, END_ZEIT);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(120);
    assertThat(student.getUrlaube()).contains(urlaub1, urlaub2, urlaub3);
  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt."
      + "K1 11:00-11:30, F1 10:30-11:30, K2 12:30-13:00, F2 12:00-13:00, U 10:00-13:30 " +
      "--> erwartet: 10-10:30 und 11:30-12:00 und 13-13:30")
  void test_35() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3);
    LocalTime bisK2 = vonK2.plusMinutes(30);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    Urlaub urlaub1 = new Urlaub(tag, START_ZEIT.plusMinutes(30), START_ZEIT.plusHours(1));
    Urlaub urlaub2 = new Urlaub(tag, START_ZEIT.plusHours(2), START_ZEIT.plusMinutes(150));
    Urlaub urlaub3 = new Urlaub(tag, START_ZEIT.plusMinutes(210), START_ZEIT.plusHours(4));
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag,
        START_ZEIT.plusMinutes(30), END_ZEIT);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(150);
    assertThat(student.getUrlaube()).contains(urlaub1, urlaub2, urlaub3);
  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt."
      + "K1 11:00-11:30, F1 10:30-11:30, K2 12:30-13:00, F2 12:00-13:00, U 11:45-12:45 " +
      "--> erwartet: 11:45-12")
  void test_36() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3);
    LocalTime bisK2 = vonK2.plusMinutes(30);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    Urlaub urlaub = new Urlaub(tag, START_ZEIT.plusMinutes(135), START_ZEIT.plusMinutes(150));
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag,
        START_ZEIT.plusMinutes(135), START_ZEIT.plusMinutes(195));
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(225);
    assertThat(student.getUrlaube()).containsExactly(urlaub);
  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt, Urlaub startet vor einer Klausur"
      + " und endet vor anderer Klausur. K1 11:00-11:30, F1 10:30-11:30, K2 12:30-13:00, "
      + "F2 12:00-13:00, U 10-12:30 --> erwartet: 10-10:30 und 11:30-12")
  void test_37() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusHours(1).plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3);
    LocalTime bisK2 = vonK2.plusMinutes(30);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    Urlaub urlaub1 = new Urlaub(tag, START_ZEIT.plusMinutes(30), START_ZEIT.plusHours(1));
    Urlaub urlaub2 = new Urlaub(tag, START_ZEIT.plusHours(2), START_ZEIT.plusMinutes(150));
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag,
        START_ZEIT.plusMinutes(30), START_ZEIT.plusHours(3));
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(180);
    assertThat(student.getUrlaube()).contains(urlaub1, urlaub2);
  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt, Urlaub startet vor einer Klausur"
      + "und endet vor anderer Klausur."
      + "K1 10:00-10:30, F1 9:30-10:30, K2 12:45-13:00, F2 12:15-13:00, U 9:30-13:30 --> erwartet: "
      + "10:30-12:15 und 13-13:30")
  void test_38() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3).plusMinutes(15);
    LocalTime bisK2 = vonK2.plusMinutes(15);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    Urlaub urlaub1 = new Urlaub(tag, START_ZEIT.plusHours(1), START_ZEIT.plusMinutes(165));
    Urlaub urlaub2 = new Urlaub(tag, START_ZEIT.plusMinutes(210), START_ZEIT.plusHours(4));
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag, START_ZEIT, END_ZEIT);
    assertThat(result).isTrue();
    assertThat(student.getRestUrlaub()).isEqualTo(105);
    assertThat(student.getUrlaube()).contains(urlaub1, urlaub2);
  }

  @Test
  @DisplayName("Aufteilung eines Urlaubs wenn es 2 Klausuren gibt, Urlaub startet vor einer Klausur"
      + "und endet vor anderer Klausur. K1 10:00-10:30, F1 9:30-10:30, K2 12:45-13:00, "
      + "F2 12:15-13:00, U 9:30-10:30 --> erwartet: Urlaub ist nicht gespeichert")
  void test_39() {
    Student student = new Student(2L, "githubname", lw);
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime vonK1 = START_ZEIT.plusMinutes(30);
    LocalTime bisK1 = vonK1.plusMinutes(30);
    LocalTime vonK2 = START_ZEIT.plusHours(3).plusMinutes(15);
    LocalTime bisK2 = vonK2.plusMinutes(15);
    Klausur klausur1 = new Klausur(1L, "Matching", 12345,
        true, tag, vonK1, bisK1);
    Klausur klausur2 = new Klausur(2L, "Data Science", 25698,
        true, tag, vonK2, bisK2);
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(studentRepository.studentMitGithubname("githubname")).thenReturn(Optional.of(student));
    studentService.klausurAnmelden("githubname", klausur1);
    studentService.klausurAnmelden("githubname", klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    boolean result = studentService.createUrlaub("githubname", tag, START_ZEIT,
        START_ZEIT.plusHours(1));
    assertThat(result).isFalse();
    assertThat(student.getRestUrlaub()).isEqualTo(240);
    assertThat(student.getUrlaube()).isEmpty();
  }
}
