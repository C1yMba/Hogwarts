package ru.hogwarts.school.interfaces;

import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.Optional;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Optional<Faculty> getFaculty(Long id);

    Faculty updateFaculty(Faculty faculty);

    void deleteFaculty(Long id);

    Collection<Faculty> findByColor(String color);
}
