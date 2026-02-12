package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // Duplicate entry 'test' for key 'employee.idx_username'
        String error_info = ex.getMessage();
        log.error("异常信息：{}", error_info);

        if(error_info.contains("Duplicate entry")){
            String[] error_info_list= error_info.split(" ");
            String error_name = error_info_list[2];
            String return_erro_info = error_name + MessageConstant.USER_EXIST;
            return Result.error(return_erro_info);
        }
        else{
            // 返回未知错误
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
