package de.checkin.web.security;

import de.checkin.application.services.AuditLogService;
import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import de.checkin.domain.klausur.Klausur;
import de.checkin.domain.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({MethodSecurityConfiguration.class})
public class IndexControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @MockBean
    KlausurService klausurService;

    @MockBean
    AuditLogService auditLogService;

    @Test
    @DisplayName("die Studenten haben Zugriff auf UebersichtSeite")
    void Test_1() throws Exception {
        Student student = new Student(1L, "Sebastian");
        List<Klausur> klausuren = List.of(new Klausur());

        when(studentService.studentMitGitHubname("Sebastian")).thenReturn(student);
        when(klausurService.getKlausuren(any())).thenReturn(klausuren);

        mockMvc.perform(get("/uebersicht")
                        .session(AuthenticationTemplates.studentSession()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("die Organisatoren haben Zugriff auf UebersichtSeite")
    void Test_2() throws Exception {
        mockMvc.perform(get("/uebersicht")
                        .session(AuthenticationTemplates.orgaSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("die Tutoren haben Zugriff auf UebersichtSeite")
    void Test_3() throws Exception {
        mockMvc.perform(get("/uebersicht")
                        .session(AuthenticationTemplates.tutorSession()))
                .andExpect(status().isForbidden());
    }
}
