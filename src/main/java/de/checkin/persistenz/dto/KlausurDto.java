package de.checkin.persistenz.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("KLAUSUR")
public record KlausurDto(
    @Id
    @Column("ID")
    Long klausurId,
    @Column("VERANSTALTUNGSNAME")
    String veranstaltungsName,
    @Column("VERANSTALTUNGSID")
    int veranstaltungsId,
    @Column("ISONLINE")
    boolean isOnline,
    LocalDate tag,
    LocalTime von,
    LocalTime bis
) {

}
