package de.checkin.domain.student;


import static de.checkin.domain.Nachricht.ANFANG_ENDE;
import static de.checkin.domain.Nachricht.ENDE_PRAKTIKUMSTAG;
import static de.checkin.domain.Nachricht.ENDZEIT_PRAKTIKUM;
import static de.checkin.domain.Nachricht.GESAMT_TAG_FREI_ODER_150_MINUTEN;
import static de.checkin.domain.Nachricht.IS_WOCHENENDE;
import static de.checkin.domain.Nachricht.RESTURLAUB;
import static de.checkin.domain.Nachricht.STARTZEIT_PRAKTIKUM;
import static de.checkin.domain.Nachricht.START_PRAKTIKUMSTAG;
import static de.checkin.domain.Nachricht.URLAUB_AN_DEM_SELBEN_TAG;
import static de.checkin.domain.Nachricht.URLAUB_VERGANGENHEIT;
import static de.checkin.domain.Nachricht.VIERTELSTUNDENFORMAT;
import static de.checkin.domain.Nachricht.VON_HINTER_BIS;
import static de.checkin.domain.Nachricht.ZWEI_URLAUBE;
import static de.checkin.domain.Validierung.END_TAG;
import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;

import de.checkin.domain.LocalDateTimeWrapper;
import de.checkin.domain.Validierung;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Student {

  private Long studentId;
  private String githubname;
  private Set<Urlaub> urlaube = new HashSet<>();
  private Set<Long> klausuren = new HashSet<>();
  private int restUrlaub = 240;
  private final List<String> nachrichten = new ArrayList<>();
  private final LocalDateTimeWrapper lw;


  public Student(Long studentId, String githubname) {
    this.studentId = studentId;
    this.githubname = githubname;
    this.lw = new LocalDateTimeWrapper();
  }

  /**
   * dieser Konstruktor ist nur für Testing gedacht, da wir einen LocalDate injecten mussten. Es ist
   * Public, weil wir mit Reflection nicht arbeiten wollten, denn wir haben ihn bei Services-Tests
   * nochmal gebraucht
   *
   * @param studentId
   * @param githubname
   * @param localDateTimeWrapper
   */
  public Student(Long studentId, String githubname, LocalDateTimeWrapper localDateTimeWrapper) {
    this.studentId = studentId;
    this.githubname = githubname;
    this.lw = localDateTimeWrapper;
  }

  public void addKlausurId(Long klausurId) {
    this.klausuren.add(klausurId);
  }

  public Set<Long> getKlausurIds() {
    return new HashSet<>(klausuren);
  }

  public int getRestUrlaub() {
    return restUrlaub;
  }

  public void setUrlaub(Urlaub urlaub) {
    this.urlaube.add(urlaub);
  }

  public void setKlausurId(Long klausurId) {
    this.klausuren.add(klausurId);
  }

  public void setRestUrlaub(int restUrlaub) {
    this.restUrlaub = restUrlaub;
  }

  /**
   * durch diese Methode kann man Urlaub buchen, und es wird der Rest-Urlaub von einem Student
   * entsprechend reduziert Dabei wird berücksichtigt, ob der Student an dem Tag eine Klausur
   * schreibt, denn wenn Ja, dann sollte der Urlaub frei geteilt werden, und da sollten viele
   * Bedingungen nicht mehr überprüft werden
   * <p>
   * sollte der Uralub nich buchbar sein, dann wird eine Rückmeldung-Nachricht generiert, und dann
   * dem User angezeigt.
   *
   * @param tag
   * @param von
   * @param bis
   * @param isKlausurVorhanden
   * @return boolean
   */
  public boolean addUrlaub(LocalDate tag, LocalTime von, LocalTime bis,
      boolean isKlausurVorhanden) {
    Urlaub urlaub = new Urlaub(tag, von, bis);
    int difference = urlaub.urlaubsDauerBerechnen();
    boolean ueberschneidung = !keineUeberschneidungen(urlaub);
    String nachricht;

    if (!isKlausurVorhanden) {

      if (getUrlaubeFuerDatum(tag).size() >= 2) {
        nachricht = ZWEI_URLAUBE.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }

      if (!ueberschneidung && getUrlaubeFuerDatum(tag).size() == 1) {
        if (!startUndEndZeitChecken(urlaub)) {
          nachricht = ANFANG_ENDE.getNachricht();
          this.nachrichten.add(nachricht);
          return false;
        } else if (abstandChecken(urlaub)) {
          nachricht = GESAMT_TAG_FREI_ODER_150_MINUTEN.getNachricht();
          this.nachrichten.add(nachricht);
          return false;
        }
      }

    }

    if (von.isBefore(START_ZEIT)) {
      nachricht = STARTZEIT_PRAKTIKUM.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (bis.isAfter(END_ZEIT)) {
      nachricht = ENDZEIT_PRAKTIKUM.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (ueberschneidung) {
      Urlaub urlaub1 = getUrlaubeFuerDatum(urlaub.getTag())
          .stream().filter(u -> u.ueberschneidungUeberpruefen(urlaub))
          .findAny().orElseThrow();

      urlaube.remove(urlaub1);
      urlaube.add(urlaub1.urlaubAddieren(urlaub));
    } else {
      if (!urlaub.isViertelStundenFormat()) {
        nachricht = VIERTELSTUNDENFORMAT.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      if (Validierung.isWochenende(tag)) {
        nachricht = IS_WOCHENENDE.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      if (von.equals(bis) || von.isAfter(bis)) {
        nachricht = VON_HINTER_BIS.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      if (difference > restUrlaub) {
        nachricht = RESTURLAUB.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      int gesamtUrlaub = gesamteUrlaubeBerechnen() + difference;
      if (gesamtUrlaub > 150 && gesamtUrlaub < 240) {
        nachricht = GESAMT_TAG_FREI_ODER_150_MINUTEN.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      if (lw.now().isAfter(tag)) {
        nachricht = URLAUB_VERGANGENHEIT.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }

      if ((tag.isEqual(lw.now()))) {
        nachricht = URLAUB_AN_DEM_SELBEN_TAG.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }

      if (tag.isAfter(END_TAG)) {
        nachricht = ENDE_PRAKTIKUMSTAG.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      if (tag.isBefore(START_TAG)) {
        nachricht = START_PRAKTIKUMSTAG.getNachricht();
        this.nachrichten.add(nachricht);
        return false;
      }
      urlaube.add(urlaub);
    }

    restUrlaub = 240 - gesamteUrlaubeBerechnen();
    return true;
  }

  private boolean keineUeberschneidungen(Urlaub urlaub) {
    List<Urlaub> urlauben = getUrlaubeFuerDatum(urlaub.getTag());
    return urlauben.stream().noneMatch(u -> u.ueberschneidungUeberpruefen(urlaub));
  }

  /**
   * Die Methode gibt alle Urlaube, die an demselben Tag sind.
   *
   * @param tag
   * @return List of Urlaub
   */
  private List<Urlaub> getUrlaubeFuerDatum(LocalDate tag) {
    return urlaube.stream().filter(u -> u.getTag().equals(tag)).collect(Collectors.toList());
  }

  public Set<Urlaub> getUrlaube() {
    return new HashSet<>(urlaube);
  }

  /**
   * Die Methode berechnet die Dauer von allen Urlaube, denn der User hat nur 240 min. Urlaub-Zeit
   *
   * @return
   */
  public int gesamteUrlaubeBerechnen() {
    int result = 0;
    for (Urlaub urlaub : urlaube) {
      result += urlaub.urlaubsDauerBerechnen();
    }
    return result;
  }

  private boolean abstandChecken(Urlaub urlaub) {
    for (Urlaub urlaub1 : urlaube) {
      if (durationBerechnen(urlaub.getVon(), urlaub1.getBis()) < 90 ||
          durationBerechnen(urlaub.getBis(), urlaub1.getVon()) < 90) {
        return true;
      }
    }

    return false;
  }

  /**
   * berechnet die Dauer zwischen zwei Uhrzeiten
   *
   * @param zeit1
   * @param zeit2
   * @return
   */
  private int durationBerechnen(LocalTime zeit1, LocalTime zeit2) {
    return Math.abs((int) Duration.between(zeit1, zeit2).toMinutes());
  }

  /**
   * Die Methode überprüft, der Urlaub innerhalb von Praktikum-Zeit liegt
   *
   * @param urlaub
   * @return
   */
  private boolean startUndEndZeitChecken(Urlaub urlaub) {
    Urlaub urlaub1 = urlaube.stream().findAny().orElseThrow();
    return (urlaub.getVon().equals(START_ZEIT) || urlaub.getBis()
        .equals(END_ZEIT)) &&
        (urlaub1.getVon().equals(START_ZEIT) || urlaub1.getBis()
            .equals(END_ZEIT));
  }

  /**
   * Der Urlaub wird anhand von dem Tag, und der Startzeit identifiziert
   *
   * @param tag
   * @param von
   * @return
   */
  public Urlaub getUrlaub(LocalDate tag, LocalTime von) {
    return this.urlaube.stream()
        .filter(urlaub -> urlaub.getTag().equals(tag) && urlaub.getVon().equals(von)).findAny()
        .orElseThrow(() -> new NoSuchElementException("Urlaub existiert nicht"));
  }

  public boolean deleteUrlaub(Urlaub urlaub) {
    if (!urlaub.isUrlaubStornierbar(lw.now())) {
      return false;
    }
    urlaube.remove(urlaub);
    restUrlaub += urlaub.urlaubsDauerBerechnen();
    return true;
  }


  public void deleteKlausur(Long klausurId) {
    klausuren.remove(klausurId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Student student)) {
      return false;
    }
    return githubname.equals(student.githubname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studentId, githubname);
  }

  public Long getStudentId() {
    return studentId;
  }

  public String getGithubname() {
    return githubname;
  }

  public LocalDateTimeWrapper getLw() {
    return lw;
  }

  public List<String> getNachrichten() {
    return new ArrayList<>(nachrichten);
  }
}