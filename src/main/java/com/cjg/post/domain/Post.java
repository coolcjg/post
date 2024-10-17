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
}
