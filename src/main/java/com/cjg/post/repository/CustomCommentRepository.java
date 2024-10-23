package com.cjg.post.repository;

import com.cjg.post.domain.Comment;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> recursiveList(Long postId);
}
