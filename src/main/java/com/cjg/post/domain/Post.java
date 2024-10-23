package com.cjg.post.domain;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="accounz_post")
@ToString
public class Post {
	
	@Id
	@Column(name ="post_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	@Column(nullable = false)
	private String title;

	@Column
	private String content;

	@Column
	private Character open;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer view;

	@CreationTimestamp
	private LocalDateTime regDate;

	private LocalDateTime modDate;

	@OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("regDate asc")
	@BatchSize(size=10)
	private List<Comment> commentList;
}
