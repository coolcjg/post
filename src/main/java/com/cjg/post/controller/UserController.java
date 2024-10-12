package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.dto.request.UserSaveRequestDto;
import com.cjg.post.dto.response.UserResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/v1/user/{userId}/count")
    public ResponseEntity<Response<Long>> count(@PathVariable("userId") String userId){
        return ResponseEntity.ok(Response.success(ResultCode.USER_SEARCH_SUCCESS, userService.count(userId)));
    }

    @PostMapping(value = "/v1/user")
    public ResponseEntity<Response<UserResponseDto>> save(@ModelAttribute @Valid UserSaveRequestDto userSaveRequestDto){
        return ResponseEntity.ok(Response.success(ResultCode.USER_SEARCH_SUCCESS, userService.save(userSaveRequestDto)));
    }


}
