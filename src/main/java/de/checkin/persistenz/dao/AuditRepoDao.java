package de.checkin.persistenz.dao;

import de.checkin.persistenz.dto.AuditLogDto;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface AuditRepoDao extends CrudRepository<AuditLogDto, Long> {

  List<AuditLogDto> findAll();
}
