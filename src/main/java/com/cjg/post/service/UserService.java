package com.cjg.post.service;

import com.cjg.post.code.ResultCode;
import com.cjg.post.config.jwt.JwtTokenProvider;
import com.cjg.post.domain.User;
import com.cjg.post.dto.request.UserLoginRequestDto;
import com.cjg.post.dto.request.UserSaveRequestDto;
import com.cjg.post.dto.response.UserLoginResponseDto;
import com.cjg.post.dto.response.UserResponseDto;
import com.cjg.post.exception.CustomException;
import com.cjg.post.repository.UserRepository;
import com.cjg.post.util.S3;
import com.cjg.post.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SHA256 sha256;

    private final S3 s3;

    private final JwtTokenProvider jwt;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public Long count(String userId){
        return userRepository.countByUserId(userId);
    }

    public UserResponseDto save(UserSaveRequestDto dto){

        User user = User.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .image(s3.upload(dto.getImage()))
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        User result = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(result.getUserId())
                .name(result.getName())
                .image(result.getImage())
                .build();
    }

    public UserLoginResponseDto login(UserLoginRequestDto requestDto){

        User user = userRepository.findByUserId(requestDto.getUserId());

        if(user == null){
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }else{

            if(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUserId(), requestDto.getPassword());
                Authentication authentication = authenticationManager.authenticate(token);
                String accessToken = jwt.createAccessToken(authentication);
                String refreshToken = jwt.createRefreshToken(authentication);

                return UserLoginResponseDto.builder()
                                .userId(user.getUserId())
                                .name(user.getName())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();

            }else {
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }

        }
    }
}
