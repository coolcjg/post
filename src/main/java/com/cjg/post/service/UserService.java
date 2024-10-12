package com.cjg.post.service;

import com.cjg.post.domain.User;
import com.cjg.post.dto.request.UserSaveRequestDto;
import com.cjg.post.dto.response.UserResponseDto;
import com.cjg.post.repository.UserRepository;
import com.cjg.post.util.S3;
import com.cjg.post.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SHA256 sha256;

    private final S3 s3;

    public Long count(String userId){
        return userRepository.countByUserId(userId);
    }

    public UserResponseDto save(UserSaveRequestDto dto){

        String salt = sha256.getSalt();

        User user = User.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .image(s3.upload(dto.getImage()))
                .salt(salt)
                .password(sha256.getEncrypt(dto.getPassword(), salt))
                .build();

        User result = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(result.getUserId())
                .name(result.getName())
                .image(result.getImage())
                .build();
    }


}
