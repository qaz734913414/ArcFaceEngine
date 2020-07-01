package com.lumotime.arcface.exception;

/**
 * <p>文件名称: FaceFeatureLibraryException </p>
 * <p>所属包名: com.lumotime.arcface.exception</p>
 * <p>描述: 人脸特征库异常 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/30 13:52 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceFeatureLibraryException extends FaceHandleException {
    public FaceFeatureLibraryException(String message) {
        super(message);
    }

    public FaceFeatureLibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
