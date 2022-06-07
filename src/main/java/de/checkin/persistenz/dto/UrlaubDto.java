package de.checkin.persistenz.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.relational.core.mapping.Table;

@Table("URLAUB")
public record UrlaubDto(LocalDate tag, LocalTime von, LocalTime bis) {

}
