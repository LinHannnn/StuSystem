package com.example.edumanage.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;  // 0表示成功，1表示失败
    private String message;
    private T data;

    private Result() {}

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 添加fail方法作为error方法的别名
    public static <T> Result<T> fail(String message) {
        return error(message);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return error(code, message);
    }
} 