package com.mall.exception;

import com.mall.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一把运行时异常转换为业务失败响应，避免前端直接红屏。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntime(RuntimeException e) {
        String msg = e.getMessage() == null || e.getMessage().isBlank() ? "操作失败" : e.getMessage();
        return Result.fail(msg);
    }
}

