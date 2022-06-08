package de.checkin.persistenz.dto;

import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("STUDENT")
public final class StudentDto {

  @Id
  @Column("ID")
  private Long studentId;
  private String githubname;
  @MappedCollection(idColumn = "STUDENT")
  private Set<UrlaubDto> urlaubs;
  @MappedCollection(idColumn = "STUDENT")
  private Set<StudentKlausurDto> klausuren;
  @Column("RESTURLAUB")
  private int restUrlaub;

  public StudentDto(Long studentId, String githubname, int restUrlaub) {
    this.studentId = studentId;
    this.githubname = githubname;
    this.restUrlaub = restUrlaub;
  }

  public Long getStudentId() {
    return studentId;
  }

  public String getGithubname() {
    return githubname;
  }

  public Set<UrlaubDto> getUrlaubs() {
    return urlaubs;
  }

  public int getRestUrlaub() {
    return restUrlaub;
  }

  public void setGithubname(String githubname) {
    this.githubname = githubname;
  }

  public void setUrlaube(Set<UrlaubDto> urlaubs) {
    this.urlaubs = urlaubs;
  }

  public Set<StudentKlausurDto> getKlausuren() {
    return klausuren;
  }

  public void addKlausuren(StudentKlausurDto klausur) {
    klausuren.add(klausur);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StudentDto that = (StudentDto) o;
    return studentId.equals(that.studentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(studentId);
  }
}
