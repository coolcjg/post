package com.cjg.post.response;



import com.cjg.post.code.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
    private final T data;

    public static <T> Response<Void> success(ResultCode resultCode){
        return new Response<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Response<T> success(ResultCode resultCode, T data){
        return new Response<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> Response<Void> fail(ResultCode resultCode){
        return new Response<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Response<Void> fail(ResultCode resultCode, String message){
        return new Response<>(resultCode.getCode(), message, null);
    }
}
