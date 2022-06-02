package de.checkin.domain;

import static de.checkin.domain.Validierung.END_TAG;
import static de.checkin.domain.Validierung.START_TAG;

public enum Nachricht {

  ZWEI_URLAUBE("Sie können kein zwei Urlaube an einem Tag machen"),
  ANFANG_ENDE("Sie dürfen nur am Anfang und am Ende des Tages Urlaub machen oder den ganzen Tag"),
  GESAMT_TAG_FREI_ODER_150_MINUTEN("Sie können entweder den gesamten Tag frei nehmen," +
      " oder bis zu zwei und halb Stunden"),
  STARTZEIT_PRAKTIKUM("Die früheste erlaubte Startzeit für Sie ist " + Validierung.START_ZEIT),
  ENDZEIT_PRAKTIKUM("Die späteste erlaubte Endzeit für Sie ist " + Validierung.END_ZEIT),
  VIERTELSTUNDENFORMAT("Es sind nur ganze Viertelstunden (d.h. 00, 15, 30 und 45) erlaubt"),
  IS_WOCHENENDE("Das Datum liegt am Wochenende"),
  VON_HINTER_BIS("Das Ende des Urlaubs muss hinter dem Anfang liegen"),
  RESTURLAUB("Das Ende des Urlaubs muss hinter dem Anfang liegen"),
  URLAUB_VERGANGENHEIT("Urlaub sollte in Zukunft liegen"),
  URLAUB_AN_DEM_SELBEN_TAG( "Sie können Urlaube nur für die Folgetage anmelden." +
      "Bitte melden Sie sich bei den Tutor:innen oder Organisator:innen," +
      "wenn Sie heute noch Urlaub benötigen oder Urlaub nachmelden wollen."),
  ENDE_PRAKTIKUMSTAG("Das Datum ist nach dem letzten Praktikumstag am " + END_TAG),
  START_PRAKTIKUMSTAG("Das Datum ist vor dem ersten Praktikumstag am " + START_TAG),
  LEERES_FELD("Bitte füllen Sie alle erforderlichen Felder aus"),
  PRAKTIKUMZEIT("Das Datum liegt ausserhalb der Praktikumszeit. " +
      "Gültig sind Mo. - Fr. im Zeitraum vom " + START_TAG + " bis " + END_TAG),
  STARTZEIT_VOR_ENDZEIT("Startzeitpunkt muss immer vor dem Endzeitpunkt liegen"),
  LSF("Die LSF Veranstaltungs-ID konnte nicht verifiziert werden. " +
      "Falls die ID korrekt ist, melden Sie sich bitte per Mail an propra@cs.hhu.de." +
      " Geben Sie alle Informationen an, die Sie in diesem Formular eingeben müssen."),
  URLAUB_NICHT_BUCHBAR("Urlaub ist nicht buchbar. Überprüfen Sie bitte Ihre Freistellungen.");

  private final String nachricht;

  Nachricht(String nachricht) {
    this.nachricht= nachricht;
  }

  public String getNachricht() {
    return nachricht;
  }
}
