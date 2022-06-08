package de.checkin.persistenz;

import static org.assertj.core.api.Assertions.assertThat;

import de.checkin.domain.student.Student;
import de.checkin.persistenz.dao.StudentRepoDao;
import de.checkin.persistenz.impl.StudentRepoImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:/db/migration/V1__init.sql", "classpath:/db/migration/V2__beispiel_daten.sql"})
public class StudentRepoImplTest {

  @Autowired
  StudentRepoDao studentRepoDao;

  StudentRepoImpl studentRepo;

  @BeforeEach
  void initial() {
    studentRepo = new StudentRepoImpl(studentRepoDao);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Student mit Id wird aus der DB hergeholt")
  void test_1() {
    Student student = studentRepo.studentMitId(1L);

    assertThat(student).isNotNull();
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Test für studentMitGithubname-Methode wenn Student in der DB besteht")
  void test_2() {
    Optional<Student> student = studentRepo.studentMitGithubname("github1");

    assertThat(student).isNotNull();
    assertThat(student.get().getStudentId()).isEqualTo(1L);
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("Test für studentMitGithubname-Methode wenn Student in der DB nicht besteht")
  void test_3() {
    Optional<Student> student = studentRepo.studentMitGithubname("github3");

    assertThat(student).isNotNull();
    assertThat(student.get().getStudentId()).isNull();
  }

  @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
  @Test
  @DisplayName("neuer Student wird in der DB gespeichert")
  void test_4() {
    Student student = new Student(null, "github4");
    studentRepo.save(student);
    Optional<Student> student1 = studentRepo.studentMitGithubname("github4");

    assertThat(student1).isNotNull();
    assertThat(student1.get().getStudentId()).isEqualTo(3L);
    assertThat(student1.get().getGithubname()).isEqualTo("github4");

  }


}
