package com.lumotime.arcface.exception;

/**
 * <p>文件名称: FaceEngineException </p>
 * <p>所属包名: com.lumotime.arcface.exception</p>
 * <p>描述: 人脸引擎异常 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/29 10:22 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceHandleException extends RuntimeException {

    public FaceHandleException(String message) {
        super(message);
    }

    public FaceHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
