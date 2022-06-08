package de.checkin.web.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.checkin.application.services.AuditLogService;
import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import de.checkin.domain.klausur.Klausur;
import de.checkin.domain.student.Student;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  KlausurService klausurService;

  @MockBean
  StudentService studentService;

  @MockBean
  AuditLogService auditLogService;

  @MockBean
  OAuth2User oAuth2User;

  @BeforeEach
  void setUp() {
    when(oAuth2User.getAttribute("login")).thenReturn("githubUsername");
    when(oAuth2User.getName()).thenReturn("githubName");

  }

  @Test
  @DisplayName("Login-Seite anzeigen für einen User, der nicht eingeloggt ist")
  void test_1() throws Exception {

    mockMvc.perform(get("/"))
        .andExpect(view().name("login"))
        .andExpect(status().is2xxSuccessful());
  }


  @Test
  @DisplayName("Uebersicht-Seite anzeigen für einen User, der eingeloggt ist")
  void test_2() throws Exception {

    mockMvc.perform(get("/")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User)))
        .andExpect(view().name("redirect:/uebersicht"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Die Uebersicht-Seite wird angezeigt, und mit Daten befüllt")
  void test_3() throws Exception {

    Student student = new Student(1L, "github");
    List<Klausur> klausuren = List.of(new Klausur());

    when(studentService.studentMitGitHubname(anyString())).thenReturn(student);
    when(klausurService.getKlausuren(any())).thenReturn(klausuren);

    mockMvc.perform(get("/uebersicht")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User)))
        .andExpect(view().name("main"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(model().attribute("klausuren", klausuren))
        .andExpect(model().attribute("student", student));

    verify(studentService).studentMitGitHubname(anyString());
    verify(klausurService).getKlausuren(any());
  }

}
