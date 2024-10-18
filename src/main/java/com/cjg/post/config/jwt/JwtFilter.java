package com.cjg.post.config.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	final String[] freePassUrl = {
			"/user/login"
			,"/v1/user/login"
			, "/js/jquery-3.7.1.js", "/css/style.css"
	};

	@Value("${cookie.domain}")
	private String cookieDomain;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String[] token = jwtTokenProvider.resolveToken(request);
		String prevAccessToken = token[0];

		if(token[0] != null && token[1] != null){

			try {
				if (jwtTokenProvider.validateToken(token)) {

					if(!prevAccessToken.equals(token[0])){

						Cookie cookie = new Cookie("accessToken", token[0]);
						cookie.setHttpOnly(true);
						cookie.setSecure(false);
						cookie.setPath("/");
						cookie.setMaxAge(60*30);
						cookie.setDomain(cookieDomain);

						response.addCookie(cookie);
					}

					Authentication auth = jwtTokenProvider.getAuthentication(token[0]);
					SecurityContextHolder.getContext().setAuthentication(auth); // 정상 토큰이면 SecurityContext에 저장
				}else{
					invalidToken(request, response);
				}
			} catch (RedisConnectionFailureException | ExpiredJwtException e) {
				SecurityContextHolder.clearContext();
				log.error(e);
				invalidToken(request, response);
			}
        }

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return StringUtils.startsWithAny(request.getRequestURI(),freePassUrl);
	}

	private void invalidToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		jwtTokenProvider.removeTokenFromCookie(request, response);
		response.sendRedirect("/user/login");
	}
}
