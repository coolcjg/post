package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.dto.request.CommentDeleteRequestDto;
import com.cjg.post.dto.request.CommentModifyRequestDto;
import com.cjg.post.dto.request.CommentSaveRequestDto;
import com.cjg.post.dto.response.CommentResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.CommentService;
import com.cjg.post.util.AuthCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final AuthCheck auth;

    @PostMapping(value = "/v1/comment")
    public ResponseEntity<Response<CommentResponseDto>> save(@RequestBody @Valid CommentSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.COMMENT_SAVE_SUCCESS, commentService.save(dto)));
    }

    @PutMapping(value = "/v1/comment")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CommentModifyRequestDto dto){
        if(auth.isSameUserForComment(customUserDetails, dto.getCommentId())){
            return ResponseEntity.ok(Response.success(ResultCode.COMMENT_MODIFY_SUCCESS, commentService.modify(dto)));
        }else{
            return ResponseEntity.status(ResultCode.COMMENT_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.COMMENT_INVALID_AUTH));
        }
    }

    @DeleteMapping(value = "/v1/comment")
    public ResponseEntity<Response<Void>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CommentDeleteRequestDto dto){
        if(auth.isSameUserForComment(customUserDetails, dto.getCommentId())){
            commentService.delete(dto);
            return ResponseEntity.ok(Response.success(ResultCode.COMMENT_DELETE_SUCCESS));
        }else{
            return ResponseEntity.status(ResultCode.COMMENT_INVALID_AUTH.getHttpStatus()).body(Response.fail(ResultCode.COMMENT_INVALID_AUTH));
        }
    }

}
