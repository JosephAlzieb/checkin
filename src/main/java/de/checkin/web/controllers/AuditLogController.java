package de.checkin.web.controllers;

import de.checkin.application.services.AuditLogService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured("ROLE_ORGA")
@RequestMapping("/auditlog")

public class AuditLogController {

  private final AuditLogService auditLogService;

  public AuditLogController(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @GetMapping
  public String getAuditUebersicht(Model model) {

    model.addAttribute("auditLog", auditLogService.findAll());
    return "auditLog";
  }
}
