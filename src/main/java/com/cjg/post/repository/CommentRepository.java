package com.cjg.post.repository;


import com.cjg.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomPostRepository  {

}
