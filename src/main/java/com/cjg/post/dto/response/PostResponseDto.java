package com.cjg.post.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostResponseDto {
    private long postId;
    private String userId;
    private String title;
    private String content;
    private char open;
    private int view;
    private String regDate;
    private String modDate;
}
