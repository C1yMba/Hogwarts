package ru.hogwarts.school.interfaces;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.FacultyDto;

import java.util.Collection;
import java.util.Optional;

public interface FacultyService {

    Faculty createFaculty(FacultyDto facultyDto);

    Optional<Faculty> getFaculty(Long id);

    Faculty updateFaculty(Long facultyId, FacultyDto facultyDto);

    void deleteFaculty(Long id);

    Collection<Faculty> findByColor(String color);

    Collection<Faculty> findByNameOrColorIgnoreCase(String value);

    Optional<Collection<Student>> findStudentsByFacultyId(Long facultyId);
}
