package de.checkin.persistenz.impl;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.domain.auditLog.AuditLog;
import de.checkin.domain.auditLog.Type;
import de.checkin.persistenz.dao.AuditRepoDao;
import de.checkin.persistenz.dto.AuditLogDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepoImpl implements AuditLogRepository {

  private final AuditRepoDao auditRepoDao;


  public AuditRepoImpl(AuditRepoDao auditRepoDao) {
    this.auditRepoDao = auditRepoDao;
  }

  @Override
  public void save(AuditLog auditLog) {
    AuditLogDto dto = new AuditLogDto(null, auditLog.student(), auditLog.type().name(),
        auditLog.tag(), auditLog.zeit());
    auditRepoDao.save(dto);
  }

  @Override
  public List<AuditLog> findAll() {
    return auditRepoDao.findAll()
        .stream()
        .map(dto -> new AuditLog(dto.id(), dto.student(), Type.valueOf(dto.type()), dto.date(),
            dto.time()))
        .collect(Collectors.toList());
  }
}
