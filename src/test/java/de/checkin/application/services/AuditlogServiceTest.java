package de.checkin.application.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.domain.auditLog.AuditLog;
import de.checkin.domain.auditLog.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuditlogServiceTest {

  private AuditLogRepository auditLogRepository = mock(AuditLogRepository.class);
  private AuditLogService auditLogService = new AuditLogService(auditLogRepository);

  @Test
  @DisplayName("Ein Auditlog kann gespeichert werden")
  void test_1() {
    AuditLog auditLog = new AuditLog(3L, "github",
        Type.LOGIN, LocalDate.of(2022, 3, 20),
        LocalTime.of(10, 0));

    auditLogService.save(auditLog);
    verify(auditLogRepository).save(auditLog);
  }
}