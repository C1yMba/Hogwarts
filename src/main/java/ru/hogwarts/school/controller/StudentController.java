package ru.hogwarts.school.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.interfaces.StudentService;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.StudentDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping()
    public Student createNewStudent(@RequestBody StudentDto newStudent) {
        return studentService.createNewStudent(newStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getExistentStudent(id);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update-student/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentDto student) {
        try {
            Student foundStudent = studentService.updateExistentStudent(id, student);
            return ResponseEntity.ok(foundStudent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteExistentStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam(required = false) int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/quantity")
    public ResponseEntity<Long> findStudentsQuantity() {
        return ResponseEntity.ok(studentService.findStudentsQuantity());
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> findStudentsAverageAge() {
        return ResponseEntity.ok(studentService.findStudentsAverageAge());
    }

    @GetMapping("/last-five")
    public ResponseEntity<Collection<Student>> findLastFiveStudents() {
        return ResponseEntity.ok(studentService.findLastFiveStudents());
    }

    @GetMapping("/between/{min_age}")
    public ResponseEntity<Collection<Student>> findStudentsWhereAgeBetweenValues(
            @PathVariable(name = "min_age", required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        if (minAge != null && maxAge != null && minAge >= 0 && maxAge >= 0 && minAge <= maxAge) {
            return ResponseEntity.ok(
                    studentService.findByAgeBetweenValues(minAge, maxAge)
            );
        }

        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/faculty/{studentId}")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long studentId) {
        Optional<Faculty> faculty = studentService.getStudentFaculty(studentId);
        return faculty.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
