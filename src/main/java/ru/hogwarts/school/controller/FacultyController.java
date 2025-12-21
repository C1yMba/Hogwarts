package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.interfaces.FacultyService;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.FacultyDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        Optional<Faculty> faculty = facultyService.getFaculty(id);
        return faculty.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody FacultyDto facultyDto) {
        return facultyService.createFaculty(facultyDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> editFaculty(
            @PathVariable Long id,
            @RequestBody FacultyDto faculty) {
        Faculty foundFaculty = facultyService.updateFaculty(id, faculty);
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam(required = false) String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/name_or_color")
    public ResponseEntity<Collection<Faculty>> findFacultiesByNameOrColor(@RequestParam(required = false) String value) {
        if (value != null && !value.isBlank()) {
            return ResponseEntity.ok(facultyService.findByNameOrColorIgnoreCase(value));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("{faculty_id}")
    public ResponseEntity<Collection<Student>> getStudentFaculty(@PathVariable Long facultyId) {
        Optional<Collection<Student>> students = facultyService.findStudentsByFacultyId(facultyId);
        return students.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
