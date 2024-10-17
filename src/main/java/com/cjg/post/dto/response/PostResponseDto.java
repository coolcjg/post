package com.cjg.post.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PostResponseDto {
    private long postId;
    private String userId;
    private String name;
    private String image;
    private String title;
    private String content;
    private char open;
    private int view;
    private String regDate;
    private String modDate;
}
