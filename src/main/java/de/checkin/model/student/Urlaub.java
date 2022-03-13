package de.checkin.model.student;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

class Urlaub {
    LocalDate tag;
    LocalTime von;
    LocalTime bis;

    public Urlaub(LocalDate tag, LocalTime von, LocalTime bis) {
        this.tag = tag;
        this.von = von;
        this.bis = bis;
    }

    public int difference(){
        return Math.abs((int) Duration.between(von, bis).toMinutes());
    }


}
