package de.checkin.application.repositories;

import de.checkin.domain.klausur.Klausur;
import java.util.List;
import java.util.Set;

public interface KlausurRepository {

  Klausur klausurMitId(Long id);

  List<Klausur> findAll();

  void save(Klausur klausur);

  List<Klausur> getKlausuren(Set<Long> klausurenIds);
}
