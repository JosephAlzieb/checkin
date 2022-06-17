package de.checkin.domain.student;

import de.checkin.domain.Validierung;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Urlaub {

  private final LocalDate tag;
  private final LocalTime von;
  private final LocalTime bis;

  public Urlaub(LocalDate tag, LocalTime von, LocalTime bis) {
    this.tag = tag;
    this.von = von;
    this.bis = bis;
  }

  /**
   * die Methode berechnet die Dauer eines Urlaubs. UrlaubEnde - UrlaubBeginn
   *
   * @return
   */
  public int urlaubsDauerBerechnen() {
    return Math.abs((int) Duration.between(von, bis).toMinutes());
  }


  public boolean isViertelStundenFormat() {
    return Validierung.zeitFormatUberprufen(von) && Validierung.zeitFormatUberprufen(bis);
  }

  /**
   * Urlaube sollten sich nicht überschneiden. wenn mehrere Urlaube um dieselbe Zeit gebucht werden,
   * dann werden die alle zusammen verschmolzen, und die Zeit nur ein mal abgezogen
   */
  public boolean ueberschneidungUeberpruefen(Urlaub urlaub) {
    return ((urlaub.von.isBefore(this.von) && urlaub.bis.isAfter(this.bis)))
        || ((urlaub.bis.isAfter(this.von) || urlaub.bis.equals(this.von)) && urlaub.von.isBefore(
        this.von))
        || ((urlaub.von.isBefore(this.bis) || urlaub.von.equals(this.bis)) && urlaub.bis.isAfter(
        this.bis));

  }

  public Urlaub urlaubAddieren(Urlaub urlaub) {
    if ((urlaub.von.isBefore(this.von) && urlaub.bis.isAfter(this.bis))) {
      return new Urlaub(tag, urlaub.von, urlaub.bis);
    } else if (((urlaub.bis.isAfter(this.von) || urlaub.bis.equals(this.von))
        && urlaub.von.isBefore(this.von))) {
      return new Urlaub(tag, urlaub.von, bis);
    } else if (((urlaub.von.isBefore(this.bis) || urlaub.von.equals(this.bis))
        && urlaub.bis.isAfter(this.bis))) {
      return new Urlaub(tag, von, urlaub.bis);
    }
    return this;
  }

  public boolean isUrlaubStornierbar(LocalDate datum) {
    return datum.isBefore(this.tag);
  }

  // Hilfsmethode für html
  public boolean isUrlaubStornierbar() {
    return LocalDate.now().isBefore(this.tag);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Urlaub urlaub = (Urlaub) o;
    return tag.equals(urlaub.tag) && von.equals(urlaub.von) && bis.equals(urlaub.bis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag, von, bis);
  }

  public LocalDate getTag() {
    return tag;
  }

  public LocalTime getVon() {
    return von;
  }

  public LocalTime getBis() {
    return bis;
  }
}