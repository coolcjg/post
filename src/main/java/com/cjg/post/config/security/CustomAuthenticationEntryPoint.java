package com.cjg.post.config.security;

import com.cjg.post.code.ResultCode;
import com.cjg.post.config.jwt.JwtTokenProvider;
import com.cjg.post.response.Response;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
			log.error("인증 에러 처리 message : " + authException.getMessage());
			jwtTokenProvider.removeTokenFromCookie(request, response);
			setResponse(response, authException);
    }
	
	private void setResponse(HttpServletResponse response, AuthenticationException authException) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().print(new Gson().toJson(Response.fail(ResultCode.INVALID_PARAM, authException.getMessage())));

	}

}
