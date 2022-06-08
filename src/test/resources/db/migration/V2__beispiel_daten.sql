insert into klausur
values (1, 'Matching', 4, true, '2022-3-23', '10:30:00', '11:30:00');
insert into klausur
values (2, 'Mathe', 7, false, '2022-3-24', '11:30:00', '13:30:00');

insert into student
values (1, 'github1', '240');
insert into student
values (2, 'github2', '180');

insert into urlaub
values ('2022-3-23', '10:30:00', '11:30:00', 1, 1);
insert into urlaub
values ('2022-3-24', '10:30:00', '11:30:00', 2, 2);

insert into auditlog
values (1, 'github1', 'LOGIN', '2022-3-23', '11:30:00');