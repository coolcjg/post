package com.cjg.post.exception;




import com.cjg.post.code.ResultCode;
import lombok.Getter;


@Getter
public class CustomException extends RuntimeException {
	
	private final ResultCode resultCode;
	
	public CustomException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}
