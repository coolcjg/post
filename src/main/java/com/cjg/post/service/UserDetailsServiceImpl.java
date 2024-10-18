package com.cjg.post.service;

import com.cjg.post.code.UserRole;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.domain.User;
import com.cjg.post.repository.UserRepository;
import com.cjg.post.util.AES256;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private  final UserRepository userRepository;

    private final AES256 aes256;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));

        return new CustomUserDetails(user.getUserId(), aes256.decrypt(user.getName()), user.getAuth(), user.getPassword(), user.getImage());
    }
}
