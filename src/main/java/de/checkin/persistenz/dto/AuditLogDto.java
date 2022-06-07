package de.checkin.persistenz.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("AUDITLOG")
public record AuditLogDto(
    @Id
    Long id,
    String student,
    String type,
    LocalDate date,
    LocalTime time
) {

}
