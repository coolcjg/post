package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.response.Response;
import com.cjg.post.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/v1/user/{userId}/count")
    public ResponseEntity<Response<Long>> count(@PathVariable("userId") String userId){
        return ResponseEntity.ok(Response.success(ResultCode.USER_SEARCH_SUCCESS, userService.count(userId)));
    }


}
