package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.dto.request.PostDeleteRequestDto;
import com.cjg.post.dto.request.PostModifyRequestDto;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.PostService;
import com.cjg.post.util.AuthCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AuthCheck auth;
    
    @PostMapping(value = "/v1/post")
    public ResponseEntity<Response<PostResponseDto>> save(@RequestBody @Valid PostSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.POST_SAVE_SUCCESS, postService.save(dto)));
    }

    @PutMapping(value = "/v1/post")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid PostModifyRequestDto dto){
        if(auth.isSameUserForPost(customUserDetails, dto.getPostId())){
            return ResponseEntity.ok(Response.success(ResultCode.POST_MODIFY_SUCCESS, postService.modify(dto)));
        }else{
            return ResponseEntity.status(ResultCode.POST_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.POST_INVALID_AUTH));
        }
    }

    @DeleteMapping(value = "/v1/post")
    public ResponseEntity<Response<Void>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid PostDeleteRequestDto dto){
        if(auth.isSameUserForPost(customUserDetails, dto.getPostId())){
            postService.delete(dto);
            return ResponseEntity.ok(Response.success(ResultCode.POST_DELETE_SUCCESS));
        }else{
            return ResponseEntity.status(ResultCode.POST_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.POST_INVALID_AUTH));
        }
    }
}
