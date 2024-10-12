package com.cjg.post.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserSaveRequestDto {

    private String userId;
    private String name;
    private String password;
    private MultipartFile image;
}
