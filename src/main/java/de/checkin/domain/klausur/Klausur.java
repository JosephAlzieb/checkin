package de.checkin.domain.klausur;

import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_ZEIT;

import java.time.LocalDate;
import java.time.LocalTime;

public class Klausur {

  private Long klausurId;
  private String veranstaltungsName;
  private int veranstaltungsId;
  private boolean isOnline = false;
  private LocalDate tag;
  private LocalTime von;
  private LocalTime bis;
  private LocalDate freistellungTag;
  private LocalTime freiStellungVon;
  private LocalTime freiStellungBis;

  public Klausur() {
  }

  /**
   * @param klausurId
   * @param veranstaltungsName
   * @param veranstaltungsId
   * @param isOnline
   * @param tag
   * @param von
   * @param bis
   */
  public Klausur(Long klausurId, String veranstaltungsName, int veranstaltungsId,
      boolean isOnline, LocalDate tag, LocalTime von, LocalTime bis) {
    this.klausurId = klausurId;
    this.veranstaltungsName = veranstaltungsName;
    this.veranstaltungsId = veranstaltungsId;
    this.isOnline = isOnline;
    this.tag = tag;
    this.von = von;
    this.bis = bis;
    freistellungAnpassen(isOnline, tag, von, bis);
  }

  public void setTag(LocalDate tag) {
    this.tag = tag;
  }

  public void setVon(LocalTime von) {
    this.von = von;
  }

  public void setBis(LocalTime bis) {
    this.bis = bis;
  }

  public Long getKlausurId() {
    return klausurId;
  }

  public String getVeranstaltungsName() {
    return veranstaltungsName;
  }

  public int getVeranstaltungsId() {
    return veranstaltungsId;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public LocalTime getVon() {
    return von;
  }

  public LocalTime getBis() {
    return bis;
  }

  public LocalTime getFreiStellungVon() {
    return freiStellungVon;
  }

  public LocalTime getFreiStellungBis() {
    return freiStellungBis;
  }

  public LocalDate getTag() {
    return tag;
  }

  public LocalDate getFreistellungTag() {
    return freistellungTag;
  }

  private void freistellungAnpassen(boolean isOnline, LocalDate tag, LocalTime von, LocalTime bis) {
    this.freistellungTag = tag;
    if (isOnline) {
      freistellungAnpassenFuerOnline(von, bis);
    }
    if (!isOnline) {
      freistellungAnpassenFuerPraesenz(von, bis);
    }
  }

  private void freistellungAnpassenFuerPraesenz(LocalTime von, LocalTime bis) {
    if (von.isAfter(START_ZEIT.plusMinutes(120))) {
      this.freiStellungVon = von.minusMinutes(120);
    } else {
      this.freiStellungVon = START_ZEIT;
    }
    if (bis.isBefore(END_ZEIT.minusMinutes(120))) {
      this.freiStellungBis = bis.plusMinutes(120);
    } else {
      this.freiStellungBis = END_ZEIT;
    }
  }

  private void freistellungAnpassenFuerOnline(LocalTime von, LocalTime bis) {
    if (von.isAfter(START_ZEIT.plusMinutes(30))) {
      this.freiStellungVon = von.minusMinutes(30);
    } else {
      this.freiStellungVon = START_ZEIT;
    }

    if (bis.isAfter(END_ZEIT)) {
      bis = END_ZEIT;
    }

    this.freiStellungBis = bis;
  }

  public boolean isKlausurStornierbar(LocalDate datum) {
    return datum.isBefore(this.tag);
  }

  @Override
  public String toString() {
    return veranstaltungsName +
        " (" + tag +
        ", " + von +
        " - " + bis +
        " Uhr)";
  }

  public String freistellungToString() {
    return " (" + freiStellungVon +
        " - " + freiStellungBis +
        " Uhr)";
  }

  // Hilfsmethode für html
  public boolean stornierbar() {
    return LocalDate.now().isBefore(this.tag);
  }
}