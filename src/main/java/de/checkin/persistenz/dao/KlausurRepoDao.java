package de.checkin.persistenz.dao;

import de.checkin.persistenz.dto.KlausurDto;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface KlausurRepoDao extends CrudRepository<KlausurDto, Long> {

  List<KlausurDto> findAll();
}
