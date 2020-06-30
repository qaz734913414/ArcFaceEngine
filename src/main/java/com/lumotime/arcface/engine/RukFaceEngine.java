package com.lumotime.arcface.engine;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectModel;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.google.common.collect.Lists;
import com.lumotime.arcface.config.EngineConfiguration;
import com.lumotime.arcface.exception.FaceHandleException;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>文件名称: RukFaceEngine </p>
 * <p>所属包名: com.lumotime.arcface.engine</p>
 * <p>描述: 自定义的人脸引擎, 简化部分操作 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/29 10:11 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class RukFaceEngine extends FaceEngine {

    /**
     * 对齐图像的时候需要进行裁剪
     */
    private static final boolean IS_ALIGNED_BITMAP_CROP = true;

    @Override
    public final int init(
            Context context,
            DetectMode detectMode,
            DetectFaceOrientPriority detectFaceOrientPriority,
            int detectFaceScaleVal,
            int detectFaceMaxNum,
            int combinedMask
    ) {
        return super.init(
                context,
                detectMode,
                detectFaceOrientPriority,
                detectFaceScaleVal,
                detectFaceMaxNum,
                combinedMask
        );
    }

    /**
     * 人脸引擎初始化业务
     *
     * @param context             调用者上下文, 内部为需要使用获取so文件路径
     * @param engineConfiguration 人脸引擎的相关配置
     * @return 初始化业务的结果
     */
    public final int init(@NonNull Context context, @NonNull EngineConfiguration engineConfiguration) {
        DetectMode detectMode = engineConfiguration.getDetectMode();
        DetectFaceOrientPriority detectFaceOrientPriority
                = engineConfiguration.getDetectFaceOrientPriority();
        int detectFaceScaleVal = engineConfiguration.getDetectFaceScaleVal();
        int detectFaceMaxNum = engineConfiguration.getDetectFaceMaxNum();
        int combinedMask = 0;
        if (engineConfiguration.getFunctionConfiguration().isSupportFaceDetect()) {
            combinedMask |= 1;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportFaceRecognition()) {
            combinedMask |= 4;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportAge()) {
            combinedMask |= 8;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportGender()) {
            combinedMask |= 16;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportFace3dAngle()) {
            combinedMask |= 32;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportLiveness()) {
            combinedMask |= 128;
        }

        if (engineConfiguration.getFunctionConfiguration().isSupportIRLiveness()) {
            combinedMask |= 1024;
        }

        return this.init(
                context.getApplicationContext(),
                detectMode,
                detectFaceOrientPriority,
                detectFaceScaleVal,
                detectFaceMaxNum,
                combinedMask
        );
    }

    /**
     * 比对两个待比对的人脸特征相似度
     *
     * @param feature  待对比的人脸特征
     * @param feature1 待对比的人脸特征
     * @return 人脸特征相似度
     */
    @Nullable
    public Float compareFace(@NonNull FaceFeature feature, @NonNull FaceFeature feature1) {
        return compareFace(feature.getFeatureData(), feature1.getFeatureData());
    }

    /**
     * 比对两个待比对的人脸特征相似度
     *
     * @param feature  待对比的人脸特征
     * @param feature1 待对比的人脸特征
     * @return 人脸特征相似度
     */
    @Nullable
    public Float compareFace(@NonNull byte[] feature, @NonNull byte[] feature1) {
        try {
            FaceFeature faceFeature = new FaceFeature();
            faceFeature.setFeatureData(feature);

            FaceFeature faceFeature1 = new FaceFeature();
            faceFeature1.setFeatureData(feature1);
            FaceSimilar faceSimilar = new FaceSimilar();
            int errorCode = compareFaceFeature(faceFeature, faceFeature1, faceSimilar);
            if (errorCode == ErrorInfo.MOK) {
                return faceSimilar.getScore();
            } else {
                Logger.e("compareFace failure, errorCode: " + errorCode);
            }
        } catch (Exception ex) {
            Logger.e(ex, "compareFace failure ");
        }
        return null;
    }

    /**
     * 从Bitmap位图画面数据中检测出的人脸列表
     *
     * @param bitmap 画面位图
     * @return 人脸信息列表
     */
    @Nullable
    public List<FaceInfo> detectFaces(@NonNull Bitmap bitmap) {
        byte[] bgr24 = bitmap2Bgr24Arrays(bitmap);
        if (bgr24 == null) {
            throw new FaceHandleException("bitmap convert bgr24 failure");
        }
        // 检测bgr24的数据中的人脸
        List<FaceInfo> faceInfoList = Lists.newArrayList();
        int alignedWidth = alignedBitmapWidth(bitmap, IS_ALIGNED_BITMAP_CROP);
        int alignedHeight = alignedBitmapHeight(bitmap, IS_ALIGNED_BITMAP_CROP);
        int errorCode = detectFaces(
                bgr24,
                alignedWidth,
                alignedHeight,
                FaceEngine.CP_PAF_BGR24,
                DetectModel.RGB,
                faceInfoList
        );
        if (errorCode == ErrorInfo.MOK) {
            return faceInfoList;
        } else {
            Logger.e("detectFaces failure, errorCode: " + errorCode);
        }
        return null;
    }

    /**
     * 从 nv21 数据中提取出面部信息
     *
     * @param nv21   nv21 视图数据
     * @param width  视图宽度
     * @param height 视图高度
     * @return 面部信息列表
     */
    public List<FaceInfo> detectFaces(@NonNull byte[] nv21, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new FaceHandleException("nv21 image width or height <= 0");
        }
        // 检测bgr24的数据中的人脸
        List<FaceInfo> faceInfoList = Lists.newArrayList();
        int errorCode = detectFaces(
                nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList
        );
        if (errorCode == ErrorInfo.MOK) {
            return faceInfoList;
        } else {
            Logger.e("detectFaces failure, errorCode: " + errorCode);
        }
        return null;
    }

    /**
     * 提取特征数据, 使用bitmap及解析的faceInfo提取出特征
     *
     * @param bitmap   位图
     * @param faceInfo 人脸信息
     * @return 人脸特征数据
     */
    @Nullable
    public FaceFeature extractFaceFeature(@NonNull Bitmap bitmap,
                                          @NonNull FaceInfo faceInfo) {

        byte[] bgr24 = bitmap2Bgr24Arrays(bitmap);
        if (bgr24 == null) {
            throw new FaceHandleException("bitmap convert bgr24 failure");
        }

        int alignedBitmapWidth = alignedBitmapWidth(bitmap, IS_ALIGNED_BITMAP_CROP);
        int alignedBitmapHeight = alignedBitmapHeight(bitmap, IS_ALIGNED_BITMAP_CROP);

        FaceFeature faceFeature = new FaceFeature();
        int errorCode = extractFaceFeature(
                bgr24,
                alignedBitmapWidth,
                alignedBitmapHeight,
                FaceEngine.CP_PAF_BGR24,
                faceInfo,
                faceFeature
        );
        if (errorCode == ErrorInfo.MOK) {
            return faceFeature;
        } else {
            Logger.e("extractFaceFeature failure, errorCode: " + errorCode);
        }
        return null;
    }

    /**
     * 提取特征数据, 使用bitmap及解析的faceInfo提取出特征
     *
     * @param nv21     nv21 画面数据
     * @param width    画面宽度
     * @param height   画面高度
     * @param faceInfo 人脸信息
     * @return 人脸特征数据
     */
    @Nullable
    public FaceFeature extractFaceFeature(@NonNull byte[] nv21, int width, int height,
                                          @NonNull FaceInfo faceInfo) {
        if (width <= 0 || height <= 0) {
            Logger.e("extractFaceFeature failure, bitmap is empty image");
            return null;
        }
        FaceFeature faceFeature = new FaceFeature();
        int errorCode = extractFaceFeature(
                nv21,
                width,
                height,
                FaceEngine.CP_PAF_NV21,
                faceInfo,
                faceFeature
        );
        if (errorCode == ErrorInfo.MOK) {
            return faceFeature;
        } else {
            Logger.e("extractFaceFeature failure, errorCode: " + errorCode);
        }
        return null;
    }

    /**
     * 将bitmap位图数据转化成bgr24数组
     *
     * @param bitmap 位图数据
     * @return bgr24数组
     */
    private static byte[] bitmap2Bgr24Arrays(Bitmap bitmap) {
        if (bitmap == null){
            throw new IllegalArgumentException("extractFaceFeature failure, bitmap is empty image");
        }
        // 将bitmap转换成bgr24
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            throw new IllegalArgumentException("extractFaceFeature failure, bitmap is empty image");
        }

        // 调整图片大小, 适合ArcFace Sdk使用BGR24输入图像
        bitmap = alignedBitmap(bitmap, IS_ALIGNED_BITMAP_CROP);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmapWidth, bitmapHeight, ArcSoftImageFormat.BGR24);
        int errorCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (errorCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            return null;
        }
        return bgr24;
    }

    /**
     * 根据ArcFace SDK对图像尺寸的限制，宽度为4的倍数, BGR24/GRAY/U16格式的图片高度不限制; 纠正对齐位图
     *
     * @param bitmap 待纠正的位图
     * @return 纠正完成后的位图(中心裁剪)
     */
    private static Bitmap alignedBitmap(@NonNull Bitmap bitmap, boolean crop) {
        return ArcSoftImageUtil.getAlignedBitmap(bitmap, crop);
    }

    /**
     * 根据ArcFace SDK对图像尺寸的限制，宽度为4的倍数, BGR24/GRAY/U16格式的图片高度不限制; 纠正对齐位图后的宽度
     * @param bitmap 待纠正的位图
     * @param crop 是否使用裁剪, 反之扩展
     * @return
     */
    private static int alignedBitmapWidth(@NonNull Bitmap bitmap, boolean crop){
        return crop ? bitmap.getWidth() & -4 : bitmap.getWidth() + (4 - (bitmap.getWidth() & 3)) % 4;
    }

    /**
     * 根据ArcFace SDK对图像尺寸的限制，宽度为4的倍数, BGR24/GRAY/U16格式的图片高度不限制; 纠正对齐位图后的高度
     * @param bitmap 待纠正的位图
     * @param crop 是否使用裁剪, 反之扩展
     * @return 是否进行
     */
    private static int alignedBitmapHeight(@NonNull Bitmap bitmap, boolean crop){
        return crop ? bitmap.getHeight() & -4 : bitmap.getHeight() + (4 - (bitmap.getHeight() & 3)) % 4;
    }
}
