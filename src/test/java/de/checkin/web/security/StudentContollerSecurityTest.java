package de.checkin.web.security;

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

import java.time.LocalDate;
import java.time.LocalTime;

import static de.checkin.domain.Validierung.START_TAG;
import static de.checkin.domain.Validierung.START_ZEIT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({MethodSecurityConfiguration.class})
public class StudentContollerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @MockBean
    KlausurService klausurService;

    @MockBean
    AuditLogService auditLogService;


    @Test
    @DisplayName("Orga hat keinen Zugriff auf UrlaubAnmeldungForm")
    void Test_1() throws Exception {
        mockMvc.perform(get("/urlaubanmeldung")
                        .session(AuthenticationTemplates.orgaSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student hat Zugriff auf UrlaubAnmeldungForm")
    void Test_2() throws Exception {
        mockMvc.perform(get("/urlaubanmeldung")
                        .session(AuthenticationTemplates.studentSession()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tutor hat keinen Zugriff auf UrlaubAnmeldungForm")
    void Test_3() throws Exception {
        mockMvc.perform(get("/urlaubanmeldung")
                        .session(AuthenticationTemplates.tutorSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Orga hat keinen Zugriff auf KlausurAnmeldungForm")
    void Test_4() throws Exception {
        mockMvc.perform(get("/klausuranmeldung")
                        .session(AuthenticationTemplates.orgaSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student hat Zugriff auf KlausurAnmeldungForm")
    void Test_5() throws Exception {
        mockMvc.perform(get("/klausuranmeldung")
                        .session(AuthenticationTemplates.studentSession()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tutor hat keinen Zugriff auf KlausurAnmeldungForm")
    void Test_6() throws Exception {
        mockMvc.perform(get("/klausuranmeldung")
                        .session(AuthenticationTemplates.tutorSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Orga hat keinen Zugriff auf UrlaubStornieren")
    void Test_7() throws Exception {
        LocalDate tag = START_TAG.plusDays(2);
        LocalTime von = START_ZEIT.plusHours(1);
        mockMvc.perform(post("/delete_Urlaub/{tag}/{von}",tag, von)
                        .session(AuthenticationTemplates.orgaSession()))
                .andExpect(status().isForbidden());
    }

}
