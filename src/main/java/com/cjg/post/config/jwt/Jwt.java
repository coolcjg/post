package com.cjg.post.config.jwt;


import com.cjg.post.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Jwt {
	
	// JWT 비밀키
	private static final String SECRET_KEY = "ChopinBlackKeyChopinBlackKeyChopinBlackKeyChopinBlackKey";
	
	private Logger logger = LoggerFactory.getLogger(Jwt.class);

	// JWT 토큰 생성
	public String createAccessToken(User user) {
		
		// Header
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		
		// Payload
		Map<String, Object> payloads = new HashMap();
		payloads.put("id", user.getUserId());
		payloads.put("name", user.getName());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, 30);		
		
		String token = Jwts.builder()
				.setClaims(payloads)
				.setIssuedAt(new Date())
				.setExpiration(new Date(cal.getTimeInMillis()))
				.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
				.compact();
		
		
		return token;
	}
	
	// JWT 리프레시 토큰 생성
	public String createRefreshToken(User user) {
		
		// Header
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");
		
		// Payload
		Map<String, Object> payloads = new HashMap();
		payloads.put("id", user.getUserId());
		payloads.put("name", user.getName());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		
		System.out.println(cal.getTime());
		
		//30DAYS
		String token = Jwts.builder()
				.setClaims(payloads)
				.setIssuedAt(new Date())
				.setExpiration(new Date(cal.getTimeInMillis()))
				.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
				.compact();
		
		
		return token;
	}	
		
	public boolean validateJwtToken(String token) {
		
		Jws<Claims> claims = Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(token);
		return true;
		
	}

	public String getUserId(String token) {
		
		Jws<Claims> claims = Jwts.parserBuilder()
								.setSigningKey(SECRET_KEY.getBytes())
								.build()
								.parseClaimsJws(token);
		
		return claims.getBody().get("id").toString();
		
	}
}
