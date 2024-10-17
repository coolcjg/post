package com.cjg.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class PostListResponseDto {
    List<PostResponseDto> list;
    int pageNumber;
    int totalPage;
    Long totalCount;
    String nextPage;
    String prevPage;

    List<Integer> pageList;
    String queryParams;
}
