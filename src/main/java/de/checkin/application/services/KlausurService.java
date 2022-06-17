package de.checkin.application.services;

import static de.checkin.domain.Nachricht.IS_WOCHENENDE;
import static de.checkin.domain.Nachricht.LEERES_FELD;
import static de.checkin.domain.Nachricht.LSF;
import static de.checkin.domain.Nachricht.PRAKTIKUMZEIT;
import static de.checkin.domain.Nachricht.STARTZEIT_VOR_ENDZEIT;
import static de.checkin.domain.Nachricht.VIERTELSTUNDENFORMAT;
import static de.checkin.domain.Validierung.END_TAG;
import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.isWochenende;
import static de.checkin.domain.Validierung.zeitFormatUberprufen;

import de.checkin.application.repositories.KlausurRepository;
import de.checkin.domain.klausur.Klausur;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class KlausurService {

  private final KlausurRepository klausurRepository;
  private final ConnectionWrapper connectionWrapper;
  private final List<String> nachrichten = new ArrayList<>();

  /**
   * dieser Konstruktor ist nur für Testing gedacht, da die Methode connect() static ist und lässt
   * sich nicht einfach mocken.
   *
   * @param klausurRepository
   * @param connectionWrapper
   */
  public KlausurService(KlausurRepository klausurRepository, ConnectionWrapper connectionWrapper) {
    this.connectionWrapper = connectionWrapper;
    this.klausurRepository = klausurRepository;
  }

  public KlausurService(KlausurRepository klausurRepository) {
    this.klausurRepository = klausurRepository;
    this.connectionWrapper = new ConnectionWrapper();
  }

  public Klausur klausurMitId(Long id) {
    return klausurRepository.klausurMitId(id);
  }

  /**
   * Die Methode erstellt eine Klausur, und wenn alle Bedingungen richtig sind, wird die Klausur
   * gespeichert
   *
   * @param veranstaltungsName
   * @param veranstaltungsId
   * @param isOnline
   * @param tag
   * @param von
   * @param bis
   * @return
   * @throws IOException
   */
  public boolean createKlausur(String veranstaltungsName, Integer veranstaltungsId,
      boolean isOnline,
      LocalDate tag, LocalTime von, LocalTime bis) throws IOException {
    String nachricht;
    if (veranstaltungsName.equals("") || veranstaltungsId == null || tag == null || von == null
        || bis == null) {
      nachricht = LEERES_FELD.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (tag.isAfter(END_TAG) || tag.isBefore(START_TAG)) {
      nachricht = PRAKTIKUMZEIT.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (isWochenende(tag)) {
      nachricht = IS_WOCHENENDE.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (von.isAfter(bis) || von.equals(bis)) {
      nachricht = STARTZEIT_VOR_ENDZEIT.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (!zeitFormatUberprufen(von) || !zeitFormatUberprufen(bis)) {
      nachricht = VIERTELSTUNDENFORMAT.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }
    if (!isLsfIdGueltig(veranstaltungsName, veranstaltungsId)) {
      nachricht = LSF.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }

    Klausur klausur = new Klausur(null, veranstaltungsName, veranstaltungsId, isOnline, tag, von,
        bis);
    klausurRepository.save(klausur);
    return true;
  }

  public List<Klausur> findAll() {
    return klausurRepository.findAll();
  }

  /**
   * Die Methode bekommt ein Set von Long, und gibt eine Liste von Klausuren zurück die Ids von
   * Klausuren, dür die der Student angemeldet ist
   *
   * @param klausurenIds
   * @return
   */
  public List<Klausur> getKlausuren(Set<Long> klausurenIds) {
    return klausurRepository.getKlausuren(klausurenIds);
  }

  private boolean isLsfIdGueltig(String veranstaltungsName, int veranstaltungsId)
      throws IOException {
    String url =
        "https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
            + veranstaltungsId
            + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung";
    String html = connectionWrapper.getHtmlResponse(url);
    String veranstaltungsID = ">" + veranstaltungsId + "<";

    return html.contains("VeranstaltungsID")
        && html.contains(veranstaltungsID)
        && html.contains(veranstaltungsName);
  }

  public List<String> getNachrichten() {
    return new ArrayList<>(nachrichten);
  }

  public void nachrichtenLeeren() {
    nachrichten.clear();
  }

}