package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.interfaces.FacultyService;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.FacultyDto;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public Faculty createFaculty(FacultyDto facultyDto) {

        Faculty newFaculty = Faculty.builder()
                .name(facultyDto.getName())
                .color(facultyDto.getColor())
                .build();
        return facultyRepository.save(newFaculty);
    }

    @Override
    public Optional<Faculty> getFaculty(Long id) {
        return facultyRepository.findById(id);
    }

    @Override
    public Faculty updateFaculty(Long facultyId, FacultyDto facultyDto) {
        Optional<Faculty> faculty = facultyRepository.findById(facultyId);
        if (faculty.isPresent()) {
            Faculty existingFaculty = faculty.get();
            if (facultyDto.getName() != null) {
                existingFaculty.setName(facultyDto.getName());
            }
            if (facultyDto.getColor() != null) {
                existingFaculty.setColor(facultyDto.getColor());
            }

            return facultyRepository.save(existingFaculty);
        } else {
            return null;
        }
    }

    @Override
    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    @Override
    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    @Override
    public Collection<Faculty> findByNameOrColorIgnoreCase(String value) {
        return facultyRepository.findByNameOrColorIgnoreCase(value);
    }

    @Override
    public Optional<Collection<Student>> findStudentsByFacultyId(Long facultyId) {
        return facultyRepository.findById(facultyId).map(Faculty::getStudents);
    }
}
