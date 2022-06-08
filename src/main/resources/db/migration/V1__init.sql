drop table if exists klausur;
drop table if exists student;
drop table if exists urlaub;
drop table if exists auditlog;


create table klausur
(
    id                 int auto_increment,
    veranstaltungsname varchar(255) not null,
    veranstaltungsid   int          not null,
    isonline           boolean      not null,
    tag                date         not null,
    von                time         not null,
    bis                time         not null,
    constraint klausur_pk
        primary key (id)
);

create table student
(
    id         int auto_increment,
    githubname varchar(255) not null,
    resturlaub int          not null,
    constraint student_pk
        primary key (id)
);

create table urlaub
(
    tag     date not null,
    von     time not null,
    bis     time not null,
    id      int auto_increment,
    constraint urlaub_pk
        primary key (id),
    student integer references student (id)
        on update cascade on delete cascade
);

create table student_klausur
(
    student integer references student (id)
        on update cascade on delete cascade,
    klausur integer references klausur (id)
        on update cascade on delete cascade,

    primary key (student, klausur)

);

create table auditlog
(
    id      int auto_increment,
    student varchar(255) not null,
    type    varchar(50)  not null,
    date    date         not null,
    time    time         not null,
    primary key (id)
);
