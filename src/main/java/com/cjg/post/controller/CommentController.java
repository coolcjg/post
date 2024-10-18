package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.dto.request.CommentModifyRequestDto;
import com.cjg.post.dto.request.CommentSaveRequestDto;
import com.cjg.post.dto.response.CommentResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/v1/comment")
    public ResponseEntity<Response<CommentResponseDto>> save(@RequestBody @Valid CommentSaveRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.COMMENT_SAVE_SUCCESS, commentService.save(dto)));
    }

    @PutMapping(value = "/v1/comment")
    public ResponseEntity<Response<CommentResponseDto>> modify(@RequestBody @Valid CommentModifyRequestDto dto){
        return ResponseEntity.ok(Response.success(ResultCode.COMMENT_MODIFY_SUCCESS, commentService.modify(dto)));
    }
}
