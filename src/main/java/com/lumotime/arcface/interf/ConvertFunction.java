package com.lumotime.arcface.interf;

import androidx.annotation.NonNull;

/**
 * 转换器函数, 用于将T 转化成R
 *
 * @param <T> 待转化数据
 * @param <R> 期望值
 */
public interface ConvertFunction<T, R> {
    /**
     * @param t 待转化数据
     * @return 期望值
     * @throws Exception 转化中的异常
     */
    R convert(@NonNull T t) throws Exception;
}