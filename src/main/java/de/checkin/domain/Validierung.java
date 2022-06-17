package de.checkin.domain;


import static de.checkin.domain.Zeit.DREISSIG;
import static de.checkin.domain.Zeit.FUENFUNDVIERZIG;
import static de.checkin.domain.Zeit.FUENFZEHN;
import static de.checkin.domain.Zeit.NULL;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Validierung {

  public static final LocalTime START_ZEIT = LocalTime.of(9, 30);
  public static final LocalTime END_ZEIT = LocalTime.of(13, 30);

  public static final LocalDate START_TAG = LocalDate.of(2022, 3, 7);
  public static final LocalDate END_TAG = LocalDate.of(2022, 3, 25);

  /**
   * prüft, ob der Tag am Wochenende liegt
   *
   * @param tag
   * @return
   */
  public static boolean isWochenende(LocalDate tag) {
    DayOfWeek tag1 = tag.getDayOfWeek();
    return tag1 == DayOfWeek.SATURDAY || tag1 == DayOfWeek.SUNDAY;
  }


  /**
   * für ein Urlaub-Anmeldung sollte nur ganze Viertelstunden (d.h. 00, 15, 30 und 45) erlaubt sein
   *
   * @param zeit
   * @return
   */
  public static boolean zeitFormatUberprufen(LocalTime zeit) {
    return zeit.getMinute() == NULL.getMinutes() ||
        zeit.getMinute() == DREISSIG.getMinutes() ||
        zeit.getMinute() == FUENFZEHN.getMinutes() ||
        zeit.getMinute() == FUENFUNDVIERZIG.getMinutes();
  }

}