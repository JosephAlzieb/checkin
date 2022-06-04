package de.checkin.application.repositories;

import de.checkin.domain.auditLog.AuditLog;
import java.util.List;

public interface AuditLogRepository {

  void save(AuditLog auditLog);

  List<AuditLog> findAll();
}
