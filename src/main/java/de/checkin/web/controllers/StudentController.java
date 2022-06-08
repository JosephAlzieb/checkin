package de.checkin.web.controllers;

import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import de.checkin.domain.klausur.Klausur;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Secured("ROLE_STUDENT")
public class StudentController {

  private final StudentService studentService;
  private final KlausurService klausurService;

  public StudentController(StudentService studentService, KlausurService klausurService) {
    this.studentService = studentService;
    this.klausurService = klausurService;
  }

  /**
   * Form für Urlaub-Anmeldung anzeigen
   */
  @GetMapping("/urlaubanmeldung")
  public String getUrlaubAnmeldungForm() {
    return "urlaubanmelden";
  }

  /**
   * Form für Klausur-Anmeldung anzeigen
   */
  @GetMapping("/klausuranmeldung")
  public String getKlausurAnmeldungForm(Model model) {
    model.addAttribute("klausuren", klausurService.findAll());
    return "klausuranmelden";
  }

  @PostMapping("/urlaubanmeldung")
  public String urlaubAnmelden(@AuthenticationPrincipal OAuth2User principal,
      @RequestParam(name = "tag") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tag,
      @RequestParam(name = "von") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime von,
      @RequestParam(name = "bis") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime bis,
      RedirectAttributes redirect) {
    String githubname = principal.getAttribute("login");
    studentService.nachrichtenLeeren();

    boolean success = studentService.createUrlaub(githubname, tag, von, bis);
    List<String> nachrichten = studentService.getNachrichten();
    if (success) {
      return "redirect:/uebersicht";
    }

    redirect.addFlashAttribute("nachrichten", nachrichten);
    return "redirect:/urlaubanmeldung";
  }

  @PostMapping("/delete_Urlaub/{tag}/{von}")
  public String urlaubStornieren(@AuthenticationPrincipal OAuth2User principal,
      @PathVariable("tag") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tag,
      @PathVariable("von") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime von) {
    String githubname = principal.getAttribute("login");
    studentService.deleteUrlaub(githubname, tag, von);
    return "redirect:/uebersicht";
  }

  @PostMapping("/klausuranmeldung")
  public String klausurAnmelden(@AuthenticationPrincipal OAuth2User principal, Long id) {
    if (id != null) {
      String githubname = principal.getAttribute("login");
      Klausur klausur = klausurService.klausurMitId(id);
      studentService.klausurAnmelden(githubname, klausur);
      return "redirect:/uebersicht";
    }
    return "redirect:/klausuranmeldung";
  }

  @PostMapping("/delete_Klausur/{id}")
  public String klausurStornieren(@AuthenticationPrincipal OAuth2User principal,
      @PathVariable("id") Long id) {
    String githubname = principal.getAttribute("login");
    studentService.deleteKlausur(githubname, id);
    return "redirect:/uebersicht";
  }

}
