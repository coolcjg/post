package com.cjg.post.config.jwt;

import com.cjg.post.code.ResultCode;
import com.cjg.post.exception.CustomException;
import com.cjg.post.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtTokenProvider {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${spring.jwt.secret}")
	private String secretKey;

	@Value("${spring.jwt.token.access-expiration-time}")
	private long accessExpirationTime;

	@Value("${spring.jwt.token.refresh-expiration-time}")
	private long refreshExpirationTime;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	/**
	 * Access 토큰 생성
	 */
	public String createAccessToken(Authentication authentication){
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + accessExpirationTime*60*1000);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	/**
	 * Refresh 토큰 생성
	 */
	public String createRefreshToken(Authentication authentication){
		Claims claims = Jwts.claims().setSubject(authentication.getName());
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + refreshExpirationTime*60*1000);

		String refreshToken = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();

		System.out.println("Redis Key : " + authentication.getName());

		// redis에 저장
		redisTemplate.opsForValue().set(
				authentication.getName(),
				refreshToken,
				refreshExpirationTime*60*1000,
				TimeUnit.MILLISECONDS
		);

		return refreshToken;
	}

	/**
	 * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
	 */
	public Authentication getAuthentication(String token) {
		String userPrincipal = Jwts.parser().
				setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody().getSubject();
		UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/**
	 * http 헤더로부터 bearer 토큰을 가져옴.
	 */
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	/**
	 * Access 토큰을 검증
	 */
	public boolean validateToken(String token){
		try{
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch(ExpiredJwtException e) {
			log.error(e.getMessage());
			throw new CustomException(ResultCode.JWT_EXPIRE);
		} catch(JwtException e) {
			log.error(e.getMessage());
			throw new CustomException(ResultCode.JWT_EXPIRE);
		}
	}
}