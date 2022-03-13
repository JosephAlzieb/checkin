package de.checkin.model.student;

import java.time.LocalTime;
import java.util.HashSet;
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

}
