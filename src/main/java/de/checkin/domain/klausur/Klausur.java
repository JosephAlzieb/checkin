package de.checkin.domain.klausur;

import static de.checkin.domain.Validierung.END_ZEIT;
import static de.checkin.domain.Validierung.START_ZEIT;

import java.time.LocalDate;
import java.time.LocalTime;

public class Klausur {

    private Long klausurId;
    private String veranstaltungsName;
    private int veranstaltungsId;
    private boolean isOnline = false;
    private LocalDate tag;
    private LocalTime von;
    private LocalTime bis;
    private LocalDate freistellungTag;
    private LocalTime freiStellungVon;
    private LocalTime freiStellungBis;

    public Klausur() {
    }

    /**
     *
     * @param klausurId
     * @param veranstaltungsName
     * @param veranstaltungsId
     * @param isOnline
     * @param tag
     * @param von
     * @param bis
     */
    public Klausur(Long klausurId, String veranstaltungsName, int veranstaltungsId,
        boolean isOnline, LocalDate tag, LocalTime von, LocalTime bis) {
        this.klausurId = klausurId;
        this.veranstaltungsName = veranstaltungsName;
        this.veranstaltungsId = veranstaltungsId;
        this.isOnline = isOnline;
        this.tag = tag;
        this.von = von;
        this.bis = bis;
        freistellungAnpassen(isOnline, tag, von, bis);
    }

    public void setTag(LocalDate tag) {
        this.tag = tag;
    }

    public void setVon(LocalTime von) {
        this.von = von;
    }

    public void setBis(LocalTime bis) {
        this.bis = bis;
    }

    public Long getKlausurId() {
        return klausurId;
    }

    public String getVeranstaltungsName() {
        return veranstaltungsName;
    }

    public int getVeranstaltungsId() {
        return veranstaltungsId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public LocalTime getVon() {
        return von;
    }

    public LocalTime getBis() {
        return bis;
    }

    public LocalTime getFreiStellungVon() {
        return freiStellungVon;
    }

    public LocalTime getFreiStellungBis() {
        return freiStellungBis;
    }

    public LocalDate getTag() {
        return tag;
    }

    public LocalDate getFreistellungTag() {
        return freistellungTag;
    }
