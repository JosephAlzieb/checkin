package de.checkin.persistenz.dao;

import de.checkin.persistenz.dto.StudentDto;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepoDao extends CrudRepository<StudentDto, Long> {

  Optional<StudentDto> findByGithubname(String githubname);

}
