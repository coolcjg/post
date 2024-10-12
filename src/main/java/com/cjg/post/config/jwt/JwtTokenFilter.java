package com.cjg.post.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final Jwt jwt;

	/*
	인증 성공시 Authentication객체를 생성하여 SecurityContextHolder에 저장한다.
	이후 실행되는 UsernamePasswordAuthenticationFilter는 이미 인증이 완료된 상태이다.
	다음 필터로 계속 전달되면서 컨트롤러까지 요청이 지나간다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException,  IOException{
		
		String token = ((HttpServletRequest) request).getHeader("accessToken");
		
		//유효한 토큰 확인
		try {
			if(token != null && jwt.validateJwtToken(token)) {
				
				String userId= jwt.getUserId(token);
				
				AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				securityContext.setAuthentication(authentication);
				SecurityContextHolder.setContext(securityContext);
			}

		}catch(ExpiredJwtException e) {
			request.setAttribute("exception", "ExpiredJwtException");			
		}catch(UnsupportedJwtException e) {
			request.setAttribute("exception", "UnsupportedJwtException");
		}catch(MalformedJwtException e) {
			request.setAttribute("exception", "MalformedJwtException");
		} catch(IllegalArgumentException e) {
			request.setAttribute("exception", "IllegalArgumentException");
		}
		
		chain.doFilter(request, response);
	}
}
