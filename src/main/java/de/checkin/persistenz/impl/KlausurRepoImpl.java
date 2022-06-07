package de.checkin.persistenz.impl;

import de.checkin.application.repositories.KlausurRepository;
import de.checkin.domain.klausur.Klausur;
import de.checkin.persistenz.dao.KlausurRepoDao;
import de.checkin.persistenz.dto.KlausurDto;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class KlausurRepoImpl implements KlausurRepository {

  private final KlausurRepoDao repoDao;

  public KlausurRepoImpl(KlausurRepoDao repoDao) {
    this.repoDao = repoDao;
  }

  private KlausurDto getDto(Klausur klausur) {
    return new KlausurDto(null, klausur.getVeranstaltungsName(), klausur.getVeranstaltungsId(),
        klausur.isOnline(), klausur.getTag(), klausur.getVon(), klausur.getBis());
  }

  @Override
  public Klausur klausurMitId(Long id) {
    KlausurDto dto = repoDao.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Es existiert kein Klausur mit der ID"));
    return getKlausur(dto);
  }

  private Klausur getKlausur(KlausurDto dto) {
    return new Klausur(dto.klausurId(), dto.veranstaltungsName(), dto.veranstaltungsId(),
        dto.isOnline(), dto.tag(), dto.von(), dto.bis());
  }

  public List<Klausur> findAll() {
    return repoDao.findAll().stream().map(this::getKlausur).collect(Collectors.toList());
  }

  @Override
  public void save(Klausur klausur) {
    KlausurDto dto = getDto(klausur);
    repoDao.save(dto);
  }

  @Override
  public List<Klausur> getKlausuren(Set<Long> klausurenIds) {
    List<Klausur> klausuren = new ArrayList<>();
    for (Long id : klausurenIds) {
      klausuren.add(klausurMitId(id));
    }
    return klausuren;
  }
}
