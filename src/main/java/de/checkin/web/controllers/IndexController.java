package de.checkin.web.controllers;

import de.checkin.application.services.AuditLogService;
import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import de.checkin.domain.auditLog.AuditLog;
import de.checkin.domain.auditLog.Type;
import de.checkin.domain.klausur.Klausur;
import de.checkin.domain.student.Student;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

  private final StudentService studentService;
  private final KlausurService klausurService;
  private final AuditLogService auditLogService;

  public IndexController(StudentService studentService, KlausurService klausurService,
      AuditLogService auditLogService) {
    this.studentService = studentService;
    this.klausurService = klausurService;
    this.auditLogService = auditLogService;
  }

  /**
   * Hier wird die Login-Seite angezeigt, wenn amn noch nicht angemeldet ist, sonst wird die
   * Übersicht-Seite angezeigt
   */
  @GetMapping("/")
  public String getLogin(Model model, @AuthenticationPrincipal OAuth2User principal) {
    model.addAttribute("user", principal != null ? principal.getAttribute("login") : null);
    if (principal == null) {
      return "login";
    }

    String githubname = principal.getAttribute("login");
    AuditLog auditLog = new AuditLog(null, githubname, Type.LOGIN, LocalDate.now(),
        LocalTime.now());
    auditLogService.save(auditLog);

    return "redirect:/uebersicht";
  }

  /**
   * Es werden alle Urlaub und Klausuren, für die ein Student angemeldet ist, angezeigt
   */
  @Secured("ROLE_STUDENT")
  @GetMapping("/uebersicht")
  public String getUebersicht(@AuthenticationPrincipal OAuth2User principal, Model model) {
    String githubname = principal.getAttribute("login");
    Student student = studentService.studentMitGitHubname(githubname);
    model.addAttribute("student", student);
    List<Klausur> klausuren = klausurService.getKlausuren(student.getKlausurIds());
    model.addAttribute("klausuren", klausuren);
    return "main";
  }
}
