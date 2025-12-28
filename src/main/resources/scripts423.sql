SELECT s.name AS student_name, s.age AS student_age, f.name AS faculty_name
FROM student s LEFT JOIN public.faculty f on s.faculty_id = f.id;

SELECT s.*
FROM student s JOIN avatar a on s.id = a.student_id;