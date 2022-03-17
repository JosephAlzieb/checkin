package de.checkin.model.student;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Student {
    private Long studentId;
    private String githubname;
    private Set<Urlaub> urlaubs;
    private Set<Long> klausuren;
    private int restUrlaub = 240;
    private final LocalTime startZeit= LocalTime.of(9,30);
    private final LocalTime endZeit= LocalTime.of(13,30);

    public Student(Long studentId, String githubname) {
        this.studentId = studentId;
        this.githubname = githubname;
        this.urlaubs = new HashSet<>();
        this.klausuren = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return studentId.equals(student.studentId) && githubname.equals(student.githubname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, githubname);
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getGithubname() {
        return githubname;
    }

    public void setGithubname(String githubname) {
        this.githubname = githubname;
    }

    public Set<Urlaub> getUrlaubs() {
        return urlaubs;
    }

    public void setUrlaubs(Set<Urlaub> urlaubs) {
        this.urlaubs = urlaubs;
    }

    public Set<Long> getKlausuren() {
        return klausuren;
    }

    public void setKlausuren(Set<Long> klausuren) {
        this.klausuren = klausuren;
    }
}
