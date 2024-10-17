package com.cjg.post.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class CommentResponseDto {

    private Long commentId;
    private Long parentId;
    private Long postId;
    private String userId;
    private String regDate;
    private String content;
}
