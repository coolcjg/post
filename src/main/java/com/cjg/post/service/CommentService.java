package com.cjg.post.service;

import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.Comment;
import com.cjg.post.domain.Post;
import com.cjg.post.domain.User;
import com.cjg.post.dto.request.CommentDeleteRequestDto;
import com.cjg.post.dto.request.CommentModifyRequestDto;
import com.cjg.post.dto.request.CommentSaveRequestDto;
import com.cjg.post.dto.response.CommentResponseDto;
import com.cjg.post.exception.CustomException;
import com.cjg.post.repository.CommentRepository;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.repository.UserRepository;
import com.cjg.post.util.AES256;
import com.cjg.post.util.DateToString;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final DateToString dateToString;
    private final AES256 aes256;

    public CommentResponseDto save(CommentSaveRequestDto dto){

        Post post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new CustomException(ResultCode.USER_SEARCH_NOT_FOUND));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .deleted('N')
                .content(dto.getContent())
                .build();

        if(dto.getParentId() != null){
            Comment comment1 = commentRepository.findById(dto.getParentId()).orElseThrow(() -> new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
            comment.setComment(comment1);
        }

        Comment result = commentRepository.save(comment);

        return commentToDto(result);
    }

    @Transactional
    public CommentResponseDto modify(CommentModifyRequestDto dto){
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(()->new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
        comment.setContent(dto.getContent());
        comment.setModDate(LocalDateTime.now());
        return commentToDto(comment);
    }

    @Transactional
    public void delete(CommentDeleteRequestDto dto){
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(()->new CustomException(ResultCode.COMMENT_SEARCH_NOT_FOUND));
        comment.setDeleted('Y');
    }

    private CommentResponseDto commentToDto(Comment comment){
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getComment() != null ? comment.getComment().getCommentId() : 0)
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .name(aes256.decrypt(comment.getUser().getName()))
                .content(comment.getContent())
                .regDate(dateToString.apply(comment.getRegDate()))
                .build();
    }
}