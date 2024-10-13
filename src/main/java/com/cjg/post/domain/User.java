package com.cjg.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="accounz_user")
public class User {
	
	@Id
	@Column(name ="user_id", length = 20)
	private String userId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 255)
	private String image;

	@CreationTimestamp
	private LocalDateTime regDate;

	private LocalDateTime modDate;
}
