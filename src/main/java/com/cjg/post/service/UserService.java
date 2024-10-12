package com.cjg.post.service;

import com.cjg.post.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long count(String userId){
        Long result = userRepository.countByUserId(userId);
        System.out.println("result : "  + result);
        return result;
    }


}
