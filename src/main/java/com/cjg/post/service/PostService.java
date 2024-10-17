package com.cjg.post.service;

import com.cjg.post.domain.Post;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.util.DateToString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final DateToString dateToString;

    public PostResponseDto save(PostSaveRequestDto dto){

        Post post = Post.builder()
                .user(userService.findByUserId(dto.getUserId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .open(dto.getOpen().charAt(0))
                .view(0)
                .build();

        Post result = postRepository.save(post);

        return PostResponseDto.builder()
                .postId(result.getPostId())
                .userId(result.getUser().getUserId())
                .title(result.getTitle())
                .content(result.getContent())
                .view(result.getView())
                .open(result.getOpen())
                .regDate(dateToString.apply(result.getRegDate()))
                .modDate(dateToString.apply(result.getModDate()))
                .build();
    }


    /*
    public PostLoginResponseDto login(PostLoginRequestDto requestDto){

        Post user = userRepository.findByPostId(requestDto.getPostId());

        if(user == null){
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }else{

            if(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {

                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(PostRole.USER.getValue()));

                PostDetails userDetails = userDetailsService.loadPostByPostname(requestDto.getPostId());

                PostnamePasswordAuthenticationToken token = new PostnamePasswordAuthenticationToken(userDetails, requestDto.getPassword(), grantedAuthorities);
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String accessToken = jwt.createAccessToken(authentication);
                String refreshToken = jwt.createRefreshToken(authentication);

                return PostLoginResponseDto.builder()
                                .userId(user.getPostId())
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
    public PostResponseDto modify(PostSaveRequestDto dto){

        Post user = userRepository.findByPostId(dto.getPostId());
        user.setName(aes256.encrypt(dto.getName()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if(dto.getImage() != null){
            String image = user.getImage().substring(user.getImage().lastIndexOf("/")+1);
            s3.deleteImageToS3(image);
            String newImage = s3.upload(dto.getImage());
            user.setImage(newImage);
        }

        return PostResponseDto.builder()
                .userId(dto.getPostId())
                .name(dto.getName())
                .image(user.getImage())
                .build();
    }

    @Transactional
    public void delete(PostDeleteRequestDto dto){
        Post user = userRepository.findByPostId(dto.getPostId());

        if(user != null){
            if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
                s3.deleteImageToS3(user.getImage().substring(user.getImage().lastIndexOf("/")+1));
                userRepository.deleteByPostId(dto.getPostId());
                SecurityContextHolder.clearContext();
            }else{
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }
        }else{
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }
    }

     */
}
