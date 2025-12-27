package ru.hogwarts.school.interfaces;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.StudentDto;

import java.util.Collection;
import java.util.Optional;

public interface StudentService {

    Student createNewStudent(StudentDto student);

    Optional<Student> getExistentStudent(Long id);

    Optional<Faculty> getStudentFaculty(Long studentId);

    Student updateExistentStudent(Long studentId, StudentDto student);

    void deleteExistentStudent(Long id);

    Collection<Student> findByAge(int age);

    Long findStudentsQuantity();

    Collection<Student> findByAgeBetweenValues(int minAge, int maxAge);

    Double findStudentsAverageAge();

    Collection<Student> findLastFiveStudents();
}
