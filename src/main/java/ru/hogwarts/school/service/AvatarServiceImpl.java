package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exceptions.PaginationException;
import ru.hogwarts.school.interfaces.AvatarService;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentsRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarServiceImpl implements AvatarService {

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentsRepository.getById(studentId);
        Path filePath = Path.of(avatarsDir, student + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        avatarRepository.save(avatar);
    }

    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Avatar findAvatar(Long id) {
        return avatarRepository.findByStudentId(id).orElse(new Avatar());
    }

    @Override
    public Page<Avatar> findAvatarsPageable(Integer pageNumber, Integer pageSize) {
        checkPaginationParameters(pageNumber, pageSize);
        if (pageNumber == null) {
            List<Avatar> all = avatarRepository.findAll();
            return new PageImpl<>(
                    all,
                    Pageable.unpaged(),
                    all.size()
            );
        } else {
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            return avatarRepository.findAll(pageRequest);
        }
    }

    void checkPaginationParameters(Integer pageNumber, Integer pageSize) {
        if ((pageNumber == null && pageSize != null) ||
                (pageNumber != null && pageSize == null)) {

            throw new PaginationException(
                    "pageNumber and pageSize need to be initialized together",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (pageNumber == null) {
            return;
        }

        if (pageNumber < 0) {
            throw new PaginationException(
                    "pageNumber can't be lower then 0",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (pageSize <= 0) {
            throw new PaginationException(
                    "pageSize can't be lower or equal 0",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}
