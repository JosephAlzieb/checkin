package de.checkin.web.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import de.checkin.domain.klausur.Klausur;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService studentService;

  @MockBean
  private KlausurService klausurService;

  @MockBean
  OAuth2User oAuth2User;

  private final String githubUsername = "GithubUsername";
  private final String githubName = "Githubname";

  @BeforeEach
  void setUp() {
    when(oAuth2User.getAttribute("login")).thenReturn(githubUsername);
    when(oAuth2User.getName()).thenReturn(githubName);
  }

  @Test
  @DisplayName("Seite zum Urlaub-Anmeldung wird angezeigt")
  @WithMockUser(username = "user")
  void test_1() throws Exception {
    mockMvc.perform(get("/urlaubanmeldung"))
        .andExpect(view().name("urlaubanmelden"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @DisplayName("Seite zum Klausur-Anmeldung wird angezeigt")
  @WithMockUser(username = "user")
  void test_2() throws Exception {
    mockMvc.perform(get("/klausuranmeldung"))
        .andExpect(view().name("klausuranmelden"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @DisplayName("Anmeldung zur Klausur wird durchgeführt")
  void test_3() throws Exception {

    Klausur klausur = new Klausur();
    when(klausurService.klausurMitId(3L)).thenReturn(klausur);
    mockMvc.perform(post("/klausuranmeldung")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User))
            .param("id", "3"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/uebersicht"));

    verify(klausurService).klausurMitId(3L);
    verify(studentService).klausurAnmelden(githubUsername, klausur);
  }


  @Test
  @DisplayName("Urlaub-Anmeldung wird durchgeführt")
  void test_4() throws Exception {

    mockMvc.perform(post("/urlaubanmeldung")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User))
            .param("tag", "2022-03-25")
            .param("von", "10:30")
            .param("bis", "11:30"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/urlaubanmeldung"));

    verify(studentService).createUrlaub(
        githubUsername,
        LocalDate.of(2022, 3, 25),
        LocalTime.of(10, 30),
        LocalTime.of(11, 30));
  }

  @Test
  @DisplayName("Klausur-Anmeldung kann storniert werden")
  void test_5() throws Exception {

    mockMvc.perform(post("/delete_Klausur/{id}", 1L)
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User)))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/uebersicht"));

    verify(studentService).deleteKlausur(
        githubUsername, 1L);
  }

  @Test
  @DisplayName("Urlaub-Anmeldung kann storniert werden")
  void test_6() throws Exception {

    LocalDate tag = LocalDate.of(2022, 3, 25);
    LocalTime von = LocalTime.of(10, 30);

    mockMvc.perform(post("/delete_Urlaub/{tag}/{von}", tag, von)
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User)))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/uebersicht"));

    verify(studentService).deleteUrlaub(
        githubUsername, tag, von);
  }

  @Test
  @DisplayName("Gueltiger Urlaub wird eingetragen und "
      + "redirect:/uebersicht wird durchgefuehrt")
  void test_7() throws Exception {
    when(studentService.createUrlaub("githubname",
        LocalDate.of(2022, 3, 25),
        LocalTime.of(10, 30), LocalTime.of(11, 30))).thenReturn(false);

    mockMvc.perform(post("/urlaubanmeldung")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User))
            .param("tag", "2022-03-25")
            .param("von", "10:30")
            .param("bis", "11:30"))
        .andExpect(view().name("redirect:/urlaubanmeldung"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Leere Form kann nicht eingtragen werden - NullpointerException behandeln")
  void test_8() throws Exception {
    mockMvc.perform(post("/klausuranmeldung")
            .with(csrf())
            .with(oauth2Login().oauth2User(oAuth2User)))
        .andExpect(view().name("redirect:/klausuranmeldung"))
        .andExpect(status().is3xxRedirection());

  }
}
