package com.cjg.post.controller;


import com.cjg.post.code.ResultCode;
import com.cjg.post.config.jwt.JwtTokenProvider;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.dto.request.UserDeleteRequestDto;
import com.cjg.post.dto.request.UserLoginRequestDto;
import com.cjg.post.dto.request.UserSaveRequestDto;
import com.cjg.post.dto.response.UserLoginResponseDto;
import com.cjg.post.dto.response.UserResponseDto;
import com.cjg.post.response.Response;
import com.cjg.post.service.UserService;
import com.cjg.post.util.AuthCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthCheck auth;

    @Value("${cookie.domain}")
    private String cookieDomain;



    @GetMapping(value = "/v1/user/{userId}/count")
    public ResponseEntity<Response<Long>> count(@PathVariable("userId") String userId){
        return ResponseEntity.ok(Response.success(ResultCode.USER_SEARCH_SUCCESS, userService.count(userId)));
    }

    @PostMapping(value = "/v1/user")
    public ResponseEntity<Response<UserResponseDto>> save(@ModelAttribute @Valid UserSaveRequestDto userSaveRequestDto){
        return ResponseEntity.ok(Response.success(ResultCode.USER_SAVE_SUCCESS, userService.save(userSaveRequestDto)));
    }

    @PostMapping(value = "/v1/user/login")
    public ResponseEntity<Response<UserLoginResponseDto>> login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto){
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto);

        ResponseCookie responseCookie = ResponseCookie.from("accessToken",userLoginResponseDto.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*30) 세션으로 설정
                .domain(cookieDomain)
                .build();

        ResponseCookie responseCookie2 = ResponseCookie.from("refreshToken",userLoginResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.maxAge(60*60*10) 세션으로 설정
                .domain(cookieDomain)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, responseCookie2.toString())
                .body(Response.success(ResultCode.USER_LOGIN_SUCCESS, userLoginResponseDto));
    }

    @GetMapping(value = "/v1/user/logout")
    public ResponseEntity<Response<Void>> logout(HttpServletRequest request, HttpServletResponse response){
        jwtTokenProvider.removeTokenFromCookie(request, response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body(Response.success(ResultCode.USER_LOGOUT_SUCCESS));
    }

    @PutMapping(value = "/v1/user")
    public ResponseEntity<Response<?>> modify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute @Valid UserSaveRequestDto dto){
        if(auth.isSameUserForUser(customUserDetails, dto.getUserId())){
            return ResponseEntity.ok(Response.success(ResultCode.USER_MODIFY_SUCCESS, userService.modify(dto)));
        }else{
            return ResponseEntity.ok(Response.success(ResultCode.USER_INVALID_AUTH));
        }

    }

    @DeleteMapping(value = "/v1/user")
    public ResponseEntity<Response<Void>> delete(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid UserDeleteRequestDto dto){
        userService.delete(dto);
        jwtTokenProvider.removeTokenFromCookie(request, response);
        return ResponseEntity.ok(Response.success(ResultCode.USER_DELETE_SUCCESS));
    }

}
