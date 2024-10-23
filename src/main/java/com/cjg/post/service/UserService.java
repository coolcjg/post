package com.cjg.post.service;

import com.cjg.post.code.ResultCode;
import com.cjg.post.code.UserRole;
import com.cjg.post.config.jwt.JwtTokenProvider;
import com.cjg.post.domain.User;
import com.cjg.post.dto.request.UserDeleteRequestDto;
import com.cjg.post.dto.request.UserLoginRequestDto;
import com.cjg.post.dto.request.UserModifyRequestDto;
import com.cjg.post.dto.request.UserSaveRequestDto;
import com.cjg.post.dto.response.UserLoginResponseDto;
import com.cjg.post.dto.response.UserResponseDto;
import com.cjg.post.exception.CustomException;
import com.cjg.post.repository.CommentRepository;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.repository.UserRepository;
import com.cjg.post.util.AES256;
import com.cjg.post.util.S3;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final S3 s3;

    private final JwtTokenProvider jwt;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsServiceImpl userDetailsService;

    private final AES256 aes256;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    public Long count(String userId){
        return userRepository.countByUserId(userId);
    }

    public UserResponseDto save(UserSaveRequestDto dto){

        User user = User.builder()
                .userId(dto.getUserId())
                .auth(UserRole.ADMIN.getValue())
                .name(aes256.encrypt(dto.getName()))
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

                UserDetails userDetails = userDetailsService.loadUserByUsername(requestDto.getUserId());

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, requestDto.getPassword(), userDetails.getAuthorities());
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
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


    @Transactional
    public UserResponseDto modify(UserModifyRequestDto dto){

        User user = userRepository.findByUserId(dto.getUserId());
        user.setName(aes256.encrypt(dto.getName()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if(dto.getImage() != null){
            String image = user.getImage().substring(user.getImage().lastIndexOf("/")+1);
            s3.deleteImageToS3(image);
            String newImage = s3.upload(dto.getImage());
            user.setImage(newImage);
        }

        return UserResponseDto.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .image(user.getImage())
                .build();
    }

    @Transactional
    public void delete(UserDeleteRequestDto dto){
        User user = userRepository.findByUserId(dto.getUserId());

        if(user != null){
            if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
                commentRepository.deleteByUserUserId(dto.getUserId());
                postRepository.deleteByUserUserId(dto.getUserId());
                s3.deleteImageToS3(user.getImage().substring(user.getImage().lastIndexOf("/")+1));
                userRepository.deleteByUserId(dto.getUserId());
                SecurityContextHolder.clearContext();
            }else{
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }
        }else{
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }
    }

    public User findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }
}
