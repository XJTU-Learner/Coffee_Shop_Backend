package org.xjtu_learner.coffee_shop.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理自定义异常
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CommonException ex) {
        ApiResponse<?> response = ApiResponse.failure(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 处理所有异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        ApiResponse<?> response = ApiResponse.failure("服务器内部错误: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

