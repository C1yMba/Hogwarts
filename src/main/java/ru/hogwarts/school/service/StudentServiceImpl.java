package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.interfaces.StudentService;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentsRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentsRepository studentsRepository;

    @Override
    public Student createNewStudent(Student student) {
        student.setId(null);
        return studentsRepository.save(student);
    }

    @Override
    public Optional<Student> getExistentStudent(Long id) {
        return studentsRepository.findById(id);
    }

    @Override
    public Student updateExistentStudent(Student student) {
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
}
