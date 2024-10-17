package com.cjg.post.repository;

import com.cjg.post.domain.Post;
import com.cjg.post.dto.request.PostListRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {

    Page<Post> list(Pageable pageable, PostListRequestDto dto);
}
