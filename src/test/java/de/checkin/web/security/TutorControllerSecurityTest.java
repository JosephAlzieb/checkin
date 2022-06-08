package de.checkin.web.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.checkin.application.services.AuditLogService;
import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({MethodSecurityConfiguration.class})
public class TutorControllerSecurityTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  StudentService studentService;

  @MockBean
  KlausurService klausurService;

  @MockBean
  AuditLogService auditLogService;

  @Test
  @DisplayName("die Tutoren haben Zugriff auf Gruppen_Seite")
  void Test_1() throws Exception {
    mockMvc.perform(get("/gruppen")
            .session(AuthenticationTemplates.tutorSession()))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("die Studenten haben keinen Zugriff auf Gruppen_Seite")
  void Test_2() throws Exception {
    mockMvc.perform(get("/gruppen")
            .session(AuthenticationTemplates.studentSession()))
        .andExpect(status().isForbidden());
  }
}
