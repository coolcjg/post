package com.cjg.post.exception;




import com.cjg.post.code.ResultCode;
import lombok.Getter;


@Getter
public class CustomViewException extends RuntimeException {

	private final ResultCode resultCode;

	public CustomViewException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

}
