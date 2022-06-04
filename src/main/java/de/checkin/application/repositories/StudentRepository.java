package de.checkin.application.repositories;

import de.checkin.domain.student.Student;
import java.util.Optional;

public interface StudentRepository {


  Student studentMitId(Long id);

  Optional<Student> studentMitGithubname(String githubname);

  void save(Student student);
}
