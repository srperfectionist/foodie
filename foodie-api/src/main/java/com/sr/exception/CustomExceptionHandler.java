package com.sr.exception;

import com.sr.utils.ServerResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author shirui
 * @date 2020/2/23
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ServerResponse handlerMaxUploadFile(MaxUploadSizeExceededException ex){
        return ServerResponse.createByErrorMessage("文件上传大小不能超过500KB，请压缩图片或者降低图片质量再上传");
    }
}
