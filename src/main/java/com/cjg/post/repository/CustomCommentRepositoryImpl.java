package com.cjg.post.repository;


import com.cjg.post.domain.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cjg.post.domain.QComment.comment;


@Repository
@AllArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> recursiveList(Long postId) {
        return jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .where(comment.post.postId.eq(postId))
                .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.regDate.asc())
                .fetch();
    }

}
