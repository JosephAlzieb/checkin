package de.checkin.persistenz.impl;

import de.checkin.application.repositories.StudentRepository;
import de.checkin.domain.student.Student;
import de.checkin.domain.student.Urlaub;
import de.checkin.persistenz.dao.StudentRepoDao;
import de.checkin.persistenz.dto.StudentDto;
import de.checkin.persistenz.dto.StudentKlausurDto;
import de.checkin.persistenz.dto.UrlaubDto;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepoImpl implements StudentRepository {

  private final StudentRepoDao repoDao;

  public StudentRepoImpl(StudentRepoDao repoDao) {
    this.repoDao = repoDao;
  }

  @Override
  public Student studentMitId(Long id) {
    StudentDto studentDto = repoDao.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Student mit der ID existiert nicht"));
    return new Student(studentDto.getStudentId(), studentDto.getGithubname());
  }

  @Override
  public Optional<Student> studentMitGithubname(String githubname) {
    Optional<StudentDto> studentDto = repoDao.findByGithubname(githubname);
    Student student;
    if (studentDto.isPresent()) {
      StudentDto dto = studentDto.get();
      student = new Student(dto.getStudentId(), dto.getGithubname());
      Set<Urlaub> urlaubs = dto.getUrlaubs()
          .stream()
          .map(urlaubDto -> new Urlaub(urlaubDto.tag(), urlaubDto.von(), urlaubDto.bis()))
          .collect(Collectors.toSet());
      urlaubs.forEach(student::setUrlaub);
      student.setRestUrlaub(dto.getRestUrlaub());
      Set<Long> klausurIds = dto.getKlausuren().stream().map(StudentKlausurDto::klausur)
          .collect(Collectors.toSet());
      klausurIds.forEach(student::setKlausurId);
      return Optional.of(student);
    }
    return Optional.of(new Student(null, githubname));
  }

  @Override
  public void save(Student student) {
    StudentDto studentDto = new StudentDto(student.getStudentId(), student.getGithubname(),
        student.getRestUrlaub());
    Set<UrlaubDto> urlaubDtos = student.getUrlaube()
        .stream()
        .map(urlaub -> new UrlaubDto(urlaub.getTag(), urlaub.getVon(), urlaub.getBis()))
        .collect(Collectors.toSet());
    studentDto.setUrlaube(urlaubDtos);
    Set<StudentKlausurDto> studentKlausurDtos = student.getKlausurIds().stream()
        .map(StudentKlausurDto::new).collect(Collectors.toSet());
    studentDto.setKlausuren(studentKlausurDtos);
    repoDao.save(studentDto);
  }

}
