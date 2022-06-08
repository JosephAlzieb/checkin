package de.checkin.web.config;

import de.checkin.application.repositories.AuditLogRepository;
import de.checkin.application.repositories.KlausurRepository;
import de.checkin.application.repositories.StudentRepository;
import de.checkin.application.services.AuditLogService;
import de.checkin.application.services.KlausurService;
import de.checkin.application.services.StudentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

  @Bean
  KlausurService klausurService(KlausurRepository repository) {
    return new KlausurService(repository);
  }

  @Bean
  AuditLogService auditLogService(AuditLogRepository repository) {
    return new AuditLogService(repository);
  }

  @Bean
  StudentService studentService(StudentRepository studentRepository, KlausurRepository klausurRepository,
      AuditLogRepository auditLogRepository) {
    return new StudentService(studentRepository, klausurRepository, auditLogRepository);
  }
}
