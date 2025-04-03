package org.xjtu_learner.coffee_shop.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ApiResponse<T> {

    // Getters and Setters
    private boolean success;    // 请求处理的状态，成功或失败
    private HttpStatus code;
    private String message;   // 请求的消息，通常是成功或失败的描述
    private T data;           // 请求的数据，泛型可以适应不同类型的数据

    // 构造方法
    public ApiResponse(boolean success, HttpStatus code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 返回成功的响应
    public static <T> ApiResponse<T> success(HttpStatus code,T data) {
        return new ApiResponse<>(true,code ,"请求成功", data);
    }

    // 返回成功的响应（使用默认 HTTP 状态码 200）
    public static <T> ApiResponse<T> success(T data) {
        return success(HttpStatus.OK, data);
    }

    // 返回失败的响应
    public static <T> ApiResponse<T> failure(HttpStatus code, String message) {
        return new ApiResponse<>(false, code,message, null);
    }

    // 返回失败的响应（使用默认 HTTP 状态码 400）
    public static <T> ApiResponse<T> failure(String message) {
        return failure(HttpStatus.BAD_REQUEST, message);
    }
}

