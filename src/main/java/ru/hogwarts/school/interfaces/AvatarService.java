package ru.hogwarts.school.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;

import java.io.IOException;

public interface AvatarService {


    void uploadAvatar(Long studentId, MultipartFile avatar) throws IOException;

    Avatar findAvatar(Long id);

    Page<Avatar> findAvatarsPageable(Integer pageNumber, Integer pageSize);
}
