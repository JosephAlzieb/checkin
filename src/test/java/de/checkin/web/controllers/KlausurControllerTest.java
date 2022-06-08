package de.checkin.web.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.checkin.application.services.KlausurService;
import de.checkin.domain.LocalDateTimeWrapper;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(KlausurController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KlausurControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  KlausurService service;

  @MockBean
  LocalDateTimeWrapper localDateTimeWrapper;


  @Test
  @DisplayName("Klausur Seite wird angezeigt")
  void test_1() throws Exception {
    mockMvc.perform(get("/klausuren"))
        .andExpect(view().name("klausur"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @DisplayName("Klausur mit ungueltigen Information wird nicht gespeichert")
  void test_2() throws Exception {
    mockMvc.perform(post("/klausuren")
            .param("veranstaltungsName", "Data Science")
            .param("veranstaltungsId", "225282")
            .param("isOnline", "true")
            .param("tag", "2022-03-25")
            .param("von", "10:10")
            .param("bis", "11:30"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/klausuren"));
    verify(service, never()).createKlausur("Data Science", 0, true, LocalDate.of(2022, 3, 25),
        LocalTime.of(10, 10), LocalTime.of(11, 30));
  }

  @Test
  @DisplayName("Klausur mit gueltigen Informationen wird gespeichert")
  void test_3() throws Exception {
    when(service.createKlausur("Data Science", 225282, true, LocalDate.of(2022, 3, 25),
        LocalTime.of(10, 30), LocalTime.of(11, 30))).thenReturn(true);

    mockMvc.perform(post("/klausuren")
            .param("veranstaltungsName", "Data Science")
            .param("veranstaltungsId", "225282")
            .param("isOnline", "true")
            .param("tag", "2022-03-25")
            .param("von", "10:30")
            .param("bis", "11:30"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/klausurangemeldet"));

    verify(service).createKlausur("Data Science", 225282, true, LocalDate.of(2022, 3, 25),
        LocalTime.of(10, 30), LocalTime.of(11, 30));
  }

  @Test
  @DisplayName("klausurangemeldet Seite wird angezeigt")
  void test_4() throws Exception {
    mockMvc.perform(get("/klausurangemeldet"))
        .andExpect(view().name("klausurangemeldet"))
        .andExpect(status().is2xxSuccessful());
  }
}
