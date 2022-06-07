package de.checkin.persistenz.dto;

import org.springframework.data.relational.core.mapping.Table;

@Table("STUDENT_KLAUSUR")
public record StudentKlausurDto(Long klausur) {

}
