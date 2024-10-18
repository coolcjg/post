package com.cjg.post.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert //view가  null로 들어가는것 방지
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="accounz_comment")
@ToString
public class Comment {

    @Id
    @Column(name ="comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("'N'")
    private Character delete;

    @CreationTimestamp
    private LocalDateTime regDate;

    private LocalDateTime modDate;














}
