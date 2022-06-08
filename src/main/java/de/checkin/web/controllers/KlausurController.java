package de.checkin.web.controllers;

import de.checkin.application.services.KlausurService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KlausurController {

  private final KlausurService service;

  public KlausurController(KlausurService service) {
    this.service = service;
  }


  @GetMapping("klausuren")
  public String klausur() {
    return "klausur";
  }

  @PostMapping("klausuren")
  public String klausurAnlegen(String veranstaltungsName, Integer veranstaltungsId,
      boolean isOnline,
      @RequestParam(name = "tag") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tag,
      @RequestParam(name = "von") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime von,
      @RequestParam(name = "bis") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime bis,
      RedirectAttributes redirect) throws IOException {
    service.nachrichtenLeeren();

    boolean success = service.createKlausur(veranstaltungsName, veranstaltungsId, isOnline, tag,
        von, bis);
    List<String> nachrichten = service.getNachrichten();

    if (success) {
      return "redirect:/klausurangemeldet";
    }

    redirect.addFlashAttribute("nachrichten", nachrichten);
    return "redirect:/klausuren";

  }

  /**
   * Nach einer erfolgreichen Klausur-Eintragung
   */
  @GetMapping("klausurangemeldet")
  public String klausurAngelegt() {
    return "klausurangemeldet";
  }

}
