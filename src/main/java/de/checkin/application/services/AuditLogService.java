package de.checkin.application.services;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.domain.auditLog.AuditLog;
import java.util.List;

public class AuditLogService {

  private final AuditLogRepository auditLogRepository;

  public AuditLogService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  /**
   * Die Methode speichert ein AuditLog
   * @param auditLog
   */
  public void save(AuditLog auditLog) {
    auditLogRepository.save(auditLog);
  }

  /**
   * @return List of AuditLog
   */
  public List<AuditLog> findAll() {
    return auditLogRepository.findAll();
  }
}