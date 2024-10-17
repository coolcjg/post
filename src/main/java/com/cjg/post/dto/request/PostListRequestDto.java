package com.cjg.post.dto.request;



import com.cjg.post.code.ResultCode;
import com.cjg.post.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class PostListRequestDto {

    private String title;
    private String content;
    private String userId;
    private String open;

    private Integer pageNumber;
    private Integer pageSize;

    public void checkParam(){
        if(title != null && title.isBlank()){
            throw new CustomException(ResultCode.POST_INVALID_TITLE);
        }

        if(content != null && content.isBlank()){
            throw new CustomException(ResultCode.POST_INVALID_CONTENT);
        }

        if(open != null && (open.length() > 1 || open.isBlank())  && !open.equals("Y") && !open.equals("N")){
            throw new CustomException(ResultCode.POST_INVALID_OPEN);
        }

        if(pageNumber <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_NUMBER);
        }

        if(pageSize <= 0 ){
            throw new CustomException(ResultCode.PAGE_INVALID_SIZE);
        }
    }
}
