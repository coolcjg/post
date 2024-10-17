package com.cjg.post.exception;


import com.cjg.post.code.ResultCode;
import com.cjg.post.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ExceptionAdvice {

    //커스텀 에러 처리
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<Response> exceptionHandler(final CustomException e){
        return ResponseEntity.status(Integer.parseInt(e.getResultCode().getCode()))
                .body(Response.fail(e.getResultCode()));
    }

    //VIEW용 에러 처리
    @ExceptionHandler({CustomViewException.class})
    public String exceptionHandler(final CustomViewException e, Model model){
        model.addAttribute("error", e.getMessage());
        return "error/403";
    }

    //Controller @Valid 유효성 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity.status(ResultCode.INVALID_PARAM.getHttpStatus())
                .body(Response.fail(ResultCode.INVALID_PARAM, errorMessage));
    }

    //잘못된 형식 체크(코드)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(ResultCode.INVALID_PARAM.getHttpStatus())
                .body(Response.fail(ResultCode.INVALID_PARAM));
    }

    //잘못된 형식 체크(서적 조회 : bookId에 특수문자 사용시)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(ResultCode.INVALID_PARAM.getHttpStatus())
                .body(Response.fail(ResultCode.INVALID_PARAM));
    }

}
