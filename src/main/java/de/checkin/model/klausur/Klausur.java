package de.checkin.model.klausur;

import java.time.LocalDateTime;

public class Klausur {
    private Long klausurId;
    private final String veranstaltungsName;
    private final int veranstaltungsId;
    private boolean isOnline = false;
    private LocalDateTime von;
    private LocalDateTime bis;

    public Klausur(Long klausurId, String veranstaltungsName, int veranstaltungsId,
                   boolean isOnline, LocalDateTime von, LocalDateTime bis) {
        this.klausurId = klausurId;
        this.veranstaltungsName = veranstaltungsName;
        this.veranstaltungsId = veranstaltungsId;
        this.isOnline = isOnline;
        freistellungAnpassen(isOnline, von, bis);
    }

    private void freistellungAnpassen(boolean isOnline, LocalDateTime von, LocalDateTime bis) {
        if (isOnline) {
            this.von = von.minusMinutes(30);
            this.bis = bis;
        } else {
            this.von = von.minusMinutes(120);
            this.bis = bis.plusMinutes(120);
        }
    }

    public LocalDateTime getVon() {
        return von;
    }

    public LocalDateTime getBis() {
        return bis;
    }

    public boolean istKlausurStornierbar(LocalDateTime datum) {
        if(datum.isBefore(this.von.minusDays(1L))) {
            return true;
        }
        return false;
    }
}
