package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    
    @PostMapping(value = "/v1/post")
    public ResponseEntity<Response<PostResponseDto>> save(@RequestBody @Valid PostSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.POST_SAVE_SUCCESS, postService.save(dto)));
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

    @PutMapping(value = "/v1/post")
    public ResponseEntity<Response<PostResponseDto>> modify(@ModelAttribute @Valid PostSaveRequestDto postSaveRequestDto){
        return ResponseEntity.ok(Response.success(ResultCode.POST_MODIFY_SUCCESS, postService.modify(postSaveRequestDto)));
    }

    @DeleteMapping(value = "/v1/post")
    public ResponseEntity<Response<Void>> delete(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid PostDeleteRequestDto dto){
        postService.delete(dto);
        jwtTokenProvider.removeTokenFromCookie(request, response);
        return ResponseEntity.ok(Response.success(ResultCode.POST_DELETE_SUCCESS));
    }

     */

}
