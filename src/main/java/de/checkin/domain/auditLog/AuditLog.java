package de.checkin.domain.auditLog;

import java.time.LocalDate;
import java.time.LocalTime;

public record AuditLog(
    Long id,
    String student,
    Type type,
    LocalDate tag,
    LocalTime zeit) {

}