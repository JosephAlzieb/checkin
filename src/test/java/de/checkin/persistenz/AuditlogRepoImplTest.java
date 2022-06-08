package de.checkin.persistenz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.checkin.domain.auditLog.AuditLog;
import de.checkin.domain.auditLog.Type;
import de.checkin.persistenz.dao.AuditRepoDao;
import de.checkin.persistenz.impl.AuditRepoImpl;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:/db/migration/V1__init.sql", "classpath:/db/migration/V2__beispiel_daten.sql"})
public class AuditlogRepoImplTest {

  @Autowired
  AuditRepoDao auditRepoDao;
  AuditRepoImpl auditRepo;

  @BeforeEach
  void initial() {
    auditRepo = new AuditRepoImpl(auditRepoDao);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Ein Auditlog wird in der DB gespeichert")
  void test_1() {
    LocalDate tag = LocalDate.of(2022, 3, 25);
    LocalTime von = LocalTime.of(12, 30);
    AuditLog auditLog = new AuditLog(1L, "github1", Type.LOGIN, tag, von);

    auditRepo.save(auditLog);

    List<AuditLog> auditLogs = auditRepo.findAll();
    assertEquals(2, auditLogs.size());
  }

}
