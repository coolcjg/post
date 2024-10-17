package com.cjg.post.util;

import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.domain.Post;
import com.cjg.post.domain.User;
import com.cjg.post.exception.CustomException;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthCheck {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean isSameUserForUser(CustomUserDetails customUserDetails, String userId){
        boolean result = false;
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException(ResultCode.USER_SEARCH_NOT_FOUND));

        if(customUserDetails != null && customUserDetails.getUsername().equals(user.getUserId())){
            result = true;
        }
        return result;
    }

    public boolean isSameUserForPost(CustomUserDetails customUserDetails, Long postId){
        boolean result = false;
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));

        if(customUserDetails != null && customUserDetails.getUsername().equals(post.getUser().getUserId())){
            result = true;
        }
        return result;
    }
}
