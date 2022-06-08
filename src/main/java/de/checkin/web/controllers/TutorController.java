package de.checkin.web.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured("ROLE_TUTOR")
@RequestMapping("/gruppen")
public class TutorController {

  @GetMapping
  public String gruppen() {

    return "gruppenAnzeigen";
  }
}
