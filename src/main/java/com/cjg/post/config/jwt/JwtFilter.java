package com.cjg.post.config.jwt;


import com.cjg.post.code.ResultCode;
import com.cjg.post.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String[] token = jwtTokenProvider.resolveToken(request);
		String prevAccessToken = token[0];

		try {
			if (token != null && jwtTokenProvider.validateToken(token)) {

				if(!prevAccessToken.equals(token[0])){

					Cookie cookie = new Cookie("accessToken", token[0]);
					cookie.setHttpOnly(true);
					cookie.setSecure(true);
					cookie.setPath("/");
					cookie.setMaxAge(60*30);
					cookie.setDomain("localhost");

					response.addCookie(cookie);
				}

				Authentication auth = jwtTokenProvider.getAuthentication(token[0]);
				SecurityContextHolder.getContext().setAuthentication(auth); // 정상 토큰이면 SecurityContext에 저장
			}
		} catch (RedisConnectionFailureException e) {
			SecurityContextHolder.clearContext();
			throw new CustomException(ResultCode.REDIS_CONNECTION);
		} catch (Exception e) {
			throw new CustomException(ResultCode.JWT_ERROR);
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return StringUtils.startsWithAny(request.getRequestURI(),
							 "/user/login"
							 , "/v1/user/login"
							 , "/js/jquery-3.7.1.js"
				             , "/css/style.css");
	}
}
