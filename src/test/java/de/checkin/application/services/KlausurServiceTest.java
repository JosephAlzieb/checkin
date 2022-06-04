package de.checkin.application.services;

import static de.checkin.domain.Validierung.END_TAG;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.checkin.application.repositories.KlausurRepository;
import de.checkin.domain.Validierung;
import de.checkin.domain.klausur.Klausur;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class KlausurServiceTest {

  private final KlausurRepository klausurRepository = mock(KlausurRepository.class);
  private final ConnectionWrapper connectionWrapper = mock(ConnectionWrapper.class);
  private final KlausurService klausurService = new KlausurService(klausurRepository);
  private final KlausurService klausurServiceMitJsoup = new KlausurService(klausurRepository,
      connectionWrapper);

  private void htmlResponseMaker() throws IOException {
    String url =
        "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
            + "225282"
            + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";

    String html = Jsoup.parse(new File("./src/test/resources/templates/datascience.html"), "UTF-8")
        .toString();
    when(connectionWrapper.getHtmlResponse(url)).thenReturn(html);
  }


  @Test
  @DisplayName("Eine gueltige Klausur kann gespeichert werden")
  void test_1() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(matching).isTrue();
  }

  @Test
  @DisplayName("Eine Klausur mit Id wird zurückgegeben")
  void test_2() {
    LocalDate tag = LocalDate.of(2022, 3, 12);
    LocalTime von = LocalTime.of(9, 15);
    LocalTime bis = LocalTime.of(10, 15);
    Klausur klausur = new Klausur(1L, "Data Science", 225282, true, tag, von, bis);
    when(klausurRepository.klausurMitId(1L)).thenReturn(klausur);
    klausurService.klausurMitId(1L);
    verify(klausurRepository).klausurMitId(1L);

  }

  @Test
  @DisplayName("Eine Klausur mit Startzeit nach der Endzeit kann nicht gespeichert werden")
  void test_3() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(2);
    LocalTime bis = START_ZEIT.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur am Wochenende kann nicht gespeichert werden")
  void test_4() throws IOException {
    LocalDate tag = START_TAG.plusDays(6);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur mit Endzeit in nicht Viertelstunden-Format kann nicht gespeichert werden")
  void test_5() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1).plusMinutes(10);
    htmlResponseMaker();
    boolean result = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur mit Startzeit in nicht Viertelstunden-Format kann nicht gespeichert werden")
  void test_6() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1).plusMinutes(10);
    LocalTime bis = START_ZEIT.plusHours(2).plusMinutes(15);
    htmlResponseMaker();
    boolean result = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur mit Startzeit gleich Endzeit kann nicht gespeichert werden")
  void test_7() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von;
    htmlResponseMaker();
    boolean result = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Klausuren werden durch ihre klausurIds zurückgegeben.")
  void test_8() {
    LocalDate tag1 = LocalDate.of(2022, 3, 22);
    LocalTime von1 = LocalTime.of(9, 15);
    LocalTime bis1 = LocalTime.of(9, 15);
    LocalDate tag2 = LocalDate.of(2022, 3, 25);
    LocalTime von2 = LocalTime.of(10, 30);
    LocalTime bis2 = LocalTime.of(11, 30);
    Klausur klausur1 = new Klausur(1L, "Data Science", 225282, true, tag1, von1, bis1);
    Klausur klausur2 = new Klausur(2L, "Matching", 12345, true, tag2, von2, bis2);
    List<Klausur> klausuren = List.of(klausur1, klausur2);
    when(klausurRepository.getKlausuren(Set.of(1L, 2L))).thenReturn(klausuren);
    assertThat(klausurService.getKlausuren(Set.of(1L, 2L))).isEqualTo(klausuren);
  }

  @Test
  @DisplayName("Eine Klausur mit ungueltiger LSF-ID kann nicht gespeichert werden")
  void test_9() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    String url =
        "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid=" + "0"
            + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";

    String html = Jsoup.parse(new File("./src/test/resources/templates/ungueltigelsfseite.html"),
        "UTF-8").toString();
    when(connectionWrapper.getHtmlResponse(url)).thenReturn(html);
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 0, true, tag, von, bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Ohne Veranstaltungsname kann kein Klausur gespeichert werden")
  void test_10() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("", 225282, true, tag, von, bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Ohne Datum des Tages kann kein Klausur gespeichert werden")
  void test_11() throws IOException {
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, null, von,
        bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Ohne Startzeit kann kein Klausur gespeichert werden")
  void test_12() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime bis = START_ZEIT.plusHours(2);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, null,
        bis);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Ohne Endzeit kann kein Klausur gespeichert werden")
  void test_13() throws IOException {
    LocalDate tag = START_TAG.plusDays(2);
    LocalTime von = START_ZEIT.plusHours(1);
    htmlResponseMaker();
    boolean matching = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        null);
    assertThat(matching).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur vor der Starstag des Praktikums kann nicht gespeichert werden")
  void test_14() throws IOException {
    LocalDate tag = Validierung.START_TAG.minusDays(1);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean result = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Eine Klausur nach der Endtag des Praktikumes kann nicht gespeichert werden")
  void test_15() throws IOException {
    LocalDate tag = END_TAG.plusDays(1);
    LocalTime von = START_ZEIT.plusHours(1);
    LocalTime bis = von.plusHours(1);
    htmlResponseMaker();
    boolean result = klausurServiceMitJsoup.createKlausur("Data Science", 225282, true, tag, von,
        bis);
    assertThat(result).isFalse();
  }
}