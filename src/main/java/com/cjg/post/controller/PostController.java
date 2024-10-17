package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.dto.request.PostDeleteRequestDto;
import com.cjg.post.dto.request.PostModifyRequestDto;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    
    @PostMapping(value = "/v1/post")
    public ResponseEntity<Response<PostResponseDto>> save(@RequestBody @Valid PostSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.POST_SAVE_SUCCESS, postService.save(dto)));
    }

    @PutMapping(value = "/v1/post")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid PostModifyRequestDto dto){
        if(postService.isSameUser(customUserDetails, dto.getPostId())){
            return ResponseEntity.ok(Response.success(ResultCode.POST_MODIFY_SUCCESS, postService.modify(dto)));
        }else{
            return ResponseEntity.ok(Response.fail(ResultCode.POST_INVALID_AUTH));
        }
    }

    @DeleteMapping(value = "/v1/post")
    public ResponseEntity<Response<Void>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid PostDeleteRequestDto dto){
        if(postService.isSameUser(customUserDetails, dto.getPostId())){
            postService.delete(dto);
            return ResponseEntity.ok(Response.success(ResultCode.POST_DELETE_SUCCESS));
        }else{
            return ResponseEntity.ok(Response.fail(ResultCode.POST_INVALID_AUTH));
        }
    }

    /*
    @PostMapping(value = "/v1/post/login")
    public ResponseEntity<Response<PostLoginResponseDto>> login(@RequestBody @Valid PostLoginRequestDto postLoginRequestDto){
        PostLoginResponseDto postLoginResponseDto = postService.login(postLoginRequestDto);

        ResponseCookie responseCookie = ResponseCookie.from("accessToken",postLoginResponseDto.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                //.maxAge(60*30) 세션으로 설정
                .domain("localhost")
                .build();

        ResponseCookie responseCookie2 = ResponseCookie.from("refreshToken",postLoginResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                //.maxAge(60*60*10) 세션으로 설정
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, responseCookie2.toString())
                .body(Response.success(ResultCode.POST_LOGIN_SUCCESS, postLoginResponseDto));
    }
     */

}
