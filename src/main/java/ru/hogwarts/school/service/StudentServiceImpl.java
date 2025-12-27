package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.interfaces.StudentService;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.dto.StudentDto;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentsRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public Student createNewStudent(StudentDto studentDto) {
        Faculty faculty = facultyRepository.findById(studentDto.getFacultyId())
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));

        Student newStudent = Student.builder()
                .name(studentDto.getName())
                .age(studentDto.getAge())
                .faculty(faculty)
                .build();

        return studentsRepository.save(newStudent);
    }

    @Override
    public Optional<Student> getExistentStudent(Long id) {
        return studentsRepository.findById(id);
    }

    @Override
    public Optional<Faculty> getStudentFaculty(Long studentId) {
        return studentsRepository.findById(studentId)
                .map(Student::getFaculty);
    }

    @Override
    public Student updateExistentStudent(Long studentId, StudentDto studentDto) {

        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        if (studentDto.getName() != null) {
            student.setName(studentDto.getName());
        }
        if (studentDto.getAge() != 0) {
            student.setAge(studentDto.getAge());
        }
        if (studentDto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(studentDto.getFacultyId())
                    .orElseThrow(() -> new EntityNotFoundException("Faculty not found"));
            student.setFaculty(faculty);
        }

        return studentsRepository.save(student);
    }

    @Override
    public void deleteExistentStudent(Long id) {
        studentsRepository.deleteById(id);
    }

    @Override
    public Collection<Student> findByAge(int age) {
        return studentsRepository.findByAge(age);
    }

    @Override
    public Long findStudentsQuantity() {
        return studentsRepository.findStudentsQuantity();
    }

    @Override
    public Double findStudentsAverageAge() {
        return studentsRepository.findStudentsAverageAge();
    }

    @Override
    public Collection<Student> findLastFiveStudents() {
        return studentsRepository.findLastFiveStudents();
    }


    @Override
    public Collection<Student> findByAgeBetweenValues(int minAge, int maxAge) {
        return studentsRepository.findByAgeBetween(minAge, maxAge);
    }
}
