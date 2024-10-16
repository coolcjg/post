package com.cjg.post.repository;

import com.cjg.post.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Long countByUserId(String userId);

    User findByUserId(String userId);

    Long deleteByUserId(String userId);

}
