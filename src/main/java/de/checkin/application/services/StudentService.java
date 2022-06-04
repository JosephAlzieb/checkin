package de.checkin.application.services;

import static de.checkin.domain.Nachricht.LEERES_FELD;
import static de.checkin.domain.Nachricht.URLAUB_NICHT_BUCHBAR;
import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_ZEIT;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.application.repositories.KlausurRepository;
import de.checkin.application.repositories.StudentRepository;
import de.checkin.domain.auditLog.AuditLog;
import de.checkin.domain.auditLog.Type;
import de.checkin.domain.klausur.Klausur;
import de.checkin.domain.student.Student;
import de.checkin.domain.student.Urlaub;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentService {

  private final StudentRepository studentRepository;
  private final KlausurRepository klausurRepository;
  private final AuditLogRepository auditLogRepository;
  private final List<String> nachrichten = new ArrayList<>();

  public StudentService(StudentRepository studentRepository, KlausurRepository klausurRepository,
      AuditLogRepository auditLogRepository) {
    this.studentRepository = studentRepository;
    this.klausurRepository = klausurRepository;
    this.auditLogRepository = auditLogRepository;
  }

  public Student studentMitId(Long id) {
    return studentRepository.studentMitId(id);
  }

  public void save(Student student) {
    studentRepository.save(student);
  }


  public boolean createUrlaub(String githubname, LocalDate tag, LocalTime von, LocalTime bis) {
    String nachricht;
    if (githubname == null || tag == null || von == null || bis == null) {
      nachricht = LEERES_FELD.getNachricht();
      this.nachrichten.add(nachricht);
      return false;
    }

    Student student = studentRepository.studentMitGithubname(githubname)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Student mit Githubname"));

    Set<Long> klausurIds = student.getKlausurIds();
    List<Klausur> klausuren = klausurRepository.getKlausuren(klausurIds).stream().
        filter(klausur -> klausur.getTag().isEqual(tag)).collect(Collectors.toList());

    if (klausuren.isEmpty()) {
      student.addUrlaub(tag, von, bis, false);
      nachrichten.addAll(student.getNachrichten());
    }

    List<Klausur> sortedKlausuren = klausuren.stream().sorted(Comparator.comparing(Klausur::getFreiStellungVon)).collect(Collectors.toList());
    if (!klausuren.isEmpty()) {
      urlaubBlockeErstellen(tag, von, bis, student, sortedKlausuren);
    }

    if (!student.getUrlaube().isEmpty()) {
      studentRepository.save(student);
      auditLogRepository.save(new AuditLog(null, githubname, Type.URLAUB_ANMELDUNG, LocalDate.now(), LocalTime.now()));
      return true;
    }

    nachricht = URLAUB_NICHT_BUCHBAR.getNachricht();
    this.nachrichten.add(nachricht);
    return false;
  }

  private void urlaubBlockeErstellen(LocalDate tag, LocalTime von, LocalTime bis, Student student,
      List<Klausur> sortedKlausuren) {

    for (Klausur klausur : sortedKlausuren) {
      von = urlaubMitKlausurAufteilen(tag, von, bis, student, klausur);

      int index = sortedKlausuren.indexOf(klausur) + 1;
      Klausur nextKlausur;
      if (sortedKlausuren.size() > index) {
        nextKlausur = sortedKlausuren.get(index);

        Set<Urlaub> urlaube = student.getUrlaube();
        for (Urlaub urlaub : urlaube) {
          if (urlaub.getVon().equals(von) || urlaub.getBis().equals(bis)) {
            student.deleteUrlaub(urlaub);
            student.addUrlaub(tag, urlaub.getVon(), nextKlausur.getFreiStellungVon(), true);
            von = nextKlausur.getFreiStellungVon();
          }
        }
      }
    }
  }

  /**
   * Wenn man sich für eine Klausur an dem Tag anmeldet, an dem man auch Urlaube buchen möchte.
   * Dann regetlt die Methode alles, so dass die Zeit von Rest-Urlaub nicht abgezogen wird,
   * wenn man wegen einer Klausur freigestellt ist
   * @param tag
   * @param von
   * @param bis
   * @param student
   * @param klausur
   * @return
   */
  private LocalTime urlaubMitKlausurAufteilen(LocalDate tag, LocalTime von, LocalTime bis, Student student,
      Klausur klausur) {

    if (!((klausur.getFreiStellungVon().isBefore(von) || klausur.getFreiStellungVon().equals(von))
        && (klausur.getFreiStellungBis().isAfter(bis) || klausur.getFreiStellungBis().equals(bis)))) {

      if (((klausur.getFreiStellungVon().isAfter(von) || klausur.getFreiStellungVon().equals(von))
          && !klausur.getFreiStellungBis().isBefore(bis))) {
        if (klausur.getFreiStellungVon().equals(bis) || bis.isAfter(
            klausur.getFreiStellungVon())) {
          student.addUrlaub(tag, von, klausur.getFreiStellungVon(), true);
          return klausur.getFreiStellungVon();
        } else {
          student.addUrlaub(tag, von, bis, true);
          return klausur.getFreiStellungBis();
        }
      } else if (
          (klausur.getFreiStellungVon().isBefore(von) || klausur.getFreiStellungVon().equals(von))
              && bis.isAfter(klausur.getFreiStellungBis())) {
        if (klausur.getFreiStellungBis().equals(von) || klausur.getFreiStellungBis()
            .isAfter(von)) {
          student.addUrlaub(tag, klausur.getFreiStellungBis(), bis, true);
        } else {
          student.addUrlaub(tag, von, bis, true);
        }
        return klausur.getFreiStellungBis();
      } else if ((klausur.getFreiStellungVon().isAfter(von) && klausur.getFreiStellungBis()
          .isBefore(bis))) {
        student.addUrlaub(tag, von, klausur.getFreiStellungVon(), true);
        student.addUrlaub(tag, klausur.getFreiStellungBis(), bis, true);
        return klausur.getFreiStellungBis();
      } else {
        student.addUrlaub(tag, von, bis, true);
        return klausur.getFreiStellungBis();
      }
    } else {
      return klausur.getFreiStellungBis();
    }
  }

  public Student studentMitGitHubname(String githubname) {
    return studentRepository.studentMitGithubname(githubname)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Student mit Githubname"));
  }

  /**
   * Die Methode löscht einen Urlaub für einen Student anhand von dem Tag, und der Startzeit von dem Urlaub
   * @param githubname
   * @param tag
   * @param von
   */
  public void deleteUrlaub(String githubname, LocalDate tag, LocalTime von) {
    Student student = studentRepository.studentMitGithubname(githubname)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Student mit Githubname"));
    Urlaub urlaub = student.getUrlaub(tag, von);
    boolean result = student.deleteUrlaub(urlaub);
    if (result) {
      studentRepository.save(student);
      auditLogRepository.save(
          new AuditLog(null, githubname, Type.URLAUB_STORNIERUNG, LocalDate.now(),
              LocalTime.now()));
    }
  }

  public void deleteKlausur(String githubname, Long klausurId) {
    Student student = studentRepository.studentMitGithubname(githubname)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Student mit Githubname"));
    Klausur klausur = klausurRepository.klausurMitId(klausurId);
    if (!klausur.getTag().isBefore(student.getLw().now()) && !klausur.getTag()
        .isEqual(student.getLw().now())) {
      student.deleteKlausur(klausurId);
      studentRepository.save(student);
      auditLogRepository.save(
          new AuditLog(null, githubname, Type.KLAUSUR_STORNIERUNG, LocalDate.now(),
              LocalTime.now()));

    }
  }

  /**
   * Nach einer Klausur-Aneldung werden alle Urlaube an dem Tag gelöscht, und neu hinzugefügt,
   * so dass die Zeit zwiscen Klausur und Urlaub angepasst wird
   * @param githubname
   * @param klausur
   */
  public void klausurAnmelden(String githubname, Klausur klausur) {
    Student student = studentRepository.studentMitGithubname(githubname)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Student mit Githubname"));
    List<Urlaub> urlaubs = student.getUrlaube().stream()
        .filter(u -> klausur.getTag().equals(u.getTag())).collect(Collectors.toList());
    urlaubs.forEach(student::deleteUrlaub);
    if (isKlausurRegistrierbar(student.getLw().now(), klausur)) {
      student.addKlausurId(klausur.getKlausurId());
      auditLogRepository.save(
          new AuditLog(null, githubname, Type.KLAUSUR_ANMELDUNG, LocalDate.now(), LocalTime.now()));
      studentRepository.save(student);
      urlaubs.forEach(u -> createUrlaub(githubname, u.getTag(), u.getVon(), u.getBis()));
    }
  }

  public List<String> getNachrichten() {
    return new ArrayList<>(nachrichten);
  }

  public void nachrichtenLeeren() {
    nachrichten.clear();
  }

  private boolean isKlausurRegistrierbar(LocalDate datum, Klausur klausur) {
    return (klausur.getFreiStellungBis().isAfter(START_ZEIT))
        && ((klausur.getFreiStellungBis().isBefore(END_ZEIT))||(klausur.getFreiStellungBis().equals(END_ZEIT)))
        && ((klausur.getFreiStellungVon().isAfter(START_ZEIT)) || (klausur.getFreiStellungVon().equals(START_ZEIT)))
        && (klausur.getFreiStellungVon().isBefore(END_ZEIT))
        && isDatumInZukunft(klausur.getTag(), datum);
  }

  private boolean isDatumInZukunft(LocalDate datum1, LocalDate datum2) {
    return datum1.isAfter(datum2);
  }
}