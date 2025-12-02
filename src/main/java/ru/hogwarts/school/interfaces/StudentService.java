package ru.hogwarts.school.interfaces;

import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.Optional;

public interface StudentService {

    Student createNewStudent(Student student);

    Optional<Student> getExistentStudent(Long id);

    Student updateExistentStudent(Student student);

    void deleteExistentStudent(Long id);

    Collection<Student> findByAge(int age);
}
