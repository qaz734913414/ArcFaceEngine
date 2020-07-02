package com.lumotime.arcface;

import android.Manifest;
import android.content.pm.FeatureInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.google.common.collect.Iterables;
import com.lumotime.arcface.config.Config;
import com.lumotime.arcface.config.EngineConfiguration;
import com.lumotime.arcface.config.FunctionConfiguration;
import com.lumotime.arcface.engine.RukFaceEngine;
import com.lumotime.arcface.exception.FaceEngineException;
import com.lumotime.arcface.exception.FaceHandleException;
import com.lumotime.arcface.pool.FaceEngineFactory;
import com.orhanobut.logger.Logger;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;
import java.util.List;

import cn.novakj.j3.core.Utils;
import cn.novakj.j3.core.util.SystemUtils;

/**
 * <p>文件名称: FaceEngineService </p>
 * <p>所属包名: com.lumotime.arcface</p>
 * <p>描述: 人脸引擎服务 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/29 09:27 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceEngineService {

    /**
     * 人脸检测框架, 用于检测摄像头人脸数据, 使用VIDEO模式
     */
    private GenericObjectPool<RukFaceEngine> faceEngineDetectPool;

    /**
     * 通用人脸比对引擎池, 用于人脸库比对, 特征值提取等业务
     */
    private GenericObjectPool<RukFaceEngine> faceEngineGeneralPool;

    public static FaceEngineService getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化人脸引擎, 请尝试在人脸检测业务前进行初始化
     * * 人脸检测、人脸特征值提取 引擎配比 1 : 3 (根据核心进行评估)
     */
    @RequiresPermission(allOf = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    })
    public FaceEngineService initFaceEngine() {
        int cpuCount = SystemUtils.getCpuCount();
        long engineCount = Math.round(cpuCount * 0.8);
        // 人脸检测引擎核心数目
        int detectEngineCount = Math.round(engineCount / 4.0F * 1.0F);
        // 人脸特征提取引擎核心数目
        int generalEngineCount = Math.round(engineCount / 4.0F * 3.0F);

        Logger.i("engineCount: " + engineCount + "; detectEngineCount: " + detectEngineCount
                + "; compareEngineCount: " + generalEngineCount);
        // 摄像头人脸检测引擎
        GenericObjectPoolConfig detectPoolConfig = new GenericObjectPoolConfig();
        detectPoolConfig.setMaxIdle(detectEngineCount);
        detectPoolConfig.setMaxTotal(detectEngineCount);
        detectPoolConfig.setMinIdle(detectEngineCount);
        detectPoolConfig.setLifo(false);
        detectPoolConfig.setJmxEnabled(false);
        EngineConfiguration detectCfg = new EngineConfiguration();
        FunctionConfiguration detectFunctionCfg = new FunctionConfiguration();
        //开启人脸检测功能
        detectFunctionCfg.setSupportFaceDetect(true);
        //开启人脸识别功能
        detectFunctionCfg.setSupportFaceRecognition(true);
        //开启年龄检测功能
        detectFunctionCfg.setSupportAge(true);
        //开启性别检测功能
        detectFunctionCfg.setSupportGender(true);
        //未开启活体检测功能(免费版引擎一年期的活体使用权限, 需要关闭活体检测后可以正常使用)
        detectCfg.setFunctionConfiguration(detectFunctionCfg);
        //图片检测模式，如果是连续帧的视频流图片，那么改成VIDEO模式
        detectCfg.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
        //调整检测人脸最大的数目
        detectCfg.setDetectFaceMaxNum(3);
        //人脸旋转角度
        detectCfg.setDetectFaceOrientPriority(DetectFaceOrientPriority.ASF_OP_0_ONLY);
        //底层库算法对象池
        //noinspection unchecked
        faceEngineDetectPool = new GenericObjectPool(new FaceEngineFactory(
                Utils.getApp().getApplicationContext(),
                Config.APP_ID, Config.APP_KEY, detectCfg), detectPoolConfig);

        // 通用人脸引擎
        GenericObjectPoolConfig generalPoolConfig = new GenericObjectPoolConfig();
        generalPoolConfig.setMaxIdle(generalEngineCount);
        generalPoolConfig.setMaxTotal(generalEngineCount);
        generalPoolConfig.setMinIdle(generalEngineCount);
        generalPoolConfig.setLifo(false);
        generalPoolConfig.setJmxEnabled(false);
        EngineConfiguration generalCfg = new EngineConfiguration();
        FunctionConfiguration generalFunctionCfg = new FunctionConfiguration();
        //开启人脸检测功能
        generalFunctionCfg.setSupportFaceDetect(true);
        //开启人脸识别功能
        generalFunctionCfg.setSupportFaceRecognition(true);
        //开启年龄检测功能
        generalFunctionCfg.setSupportAge(true);
        //开启性别检测功能
        generalFunctionCfg.setSupportGender(true);
        //未开启活体检测功能(免费版引擎一年期的活体使用权限, 需要关闭活体检测后可以正常使用)
        generalCfg.setFunctionConfiguration(generalFunctionCfg);
        //图片检测模式，如果是连续帧的视频流图片，那么改成VIDEO模式
        generalCfg.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        //人脸旋转角度 通用引擎检测全部的角度
        generalCfg.setDetectFaceOrientPriority(DetectFaceOrientPriority.ASF_OP_ALL_OUT);
        //底层库算法对象池
        //noinspection unchecked
        faceEngineGeneralPool = new GenericObjectPool(new FaceEngineFactory(
                Utils.getApp().getApplicationContext(),
                Config.APP_ID, Config.APP_KEY, detectCfg), generalPoolConfig);
        return this;
    }

    /**
     * 获取人脸检测引擎池
     * @return 人脸检测的引擎池
     */
    protected GenericObjectPool<RukFaceEngine> getFaceEngineDetectPool() {
        return faceEngineDetectPool;
    }

    /**
     * 获取通用人脸比对引擎池
     * @return 通用人脸比对引擎池
     */
    public GenericObjectPool<RukFaceEngine> getFaceEngineGeneralPool() {
        return faceEngineGeneralPool;
    }

    /**
     * 检测预览的图像数据中人脸信息列表
     * @param nv21Image 预览数据(nv21)
     * @param width 预览数据宽度
     * @param height 预览数据高度
     * @return 检测到预览的数据中的人脸信息列表
     */
    @Nullable
    public FaceInfo previewDetectFaceInfo(@NonNull byte[] nv21Image, int width, int height)
            throws Exception{
        return Iterables.getFirst(previewDetectFaceInfoList(nv21Image, width, height), null);
    }

    /**
     * 检测预览的图像数据中人脸信息列表
     * @param nv21Image 预览数据(nv21)
     * @param width 预览数据宽度
     * @param height 预览数据高度
     * @return 检测到预览的数据中的人脸信息列表
     */
    @Nullable
    public List<FaceInfo> previewDetectFaceInfoList(@NonNull byte[] nv21Image, int width, int height)
            throws Exception{
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineDetectPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            return faceEngine.detectFaces(nv21Image, width, height);
        } finally {
            if (faceEngine != null) {
                faceEngineDetectPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 比对两个位图中最大两张脸的特征信息
     * @param nv21Image 待比对位图
     * @param width nv21 待比对位图的宽度
     * @param height nv21 待比对位图的高度
     * @param bitmap 待比对位图
     * @return 位图中各最大的一张脸的特征相似度
     */
    @Nullable
    public Float compareFace(@NonNull byte[] nv21Image, int width, int height, @NonNull Bitmap bitmap)
        throws Exception{
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }

            // 获取第一张位图的人脸信息及特征信息
            List<FaceInfo> faceInfoList = detectFaces(nv21Image, width, height);
            FaceInfo faceInfo;
            if (isListEmpty(faceInfoList) || (faceInfo = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("nv21 detect faces list failure");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(nv21Image, width, height, faceInfo);
            if (faceFeature == null){
                throw new FaceHandleException("nv21 extract face feature failure");
            }

            // 获取第二张位图的人脸信息及特征信息
            List<FaceInfo> faceInfoList1 = detectFaces(bitmap);
            FaceInfo faceInfo1;
            if (isListEmpty(faceInfoList1) || (faceInfo1 = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("second bitmap detect faces list failure");
            }
            FaceFeature faceFeature1 = faceEngine.extractFaceFeature(bitmap, faceInfo1);
            if (faceFeature1 == null){
                throw new FaceHandleException("second bitmap extract face feature failure");
            }
            return faceEngine.compareFace(faceFeature, faceFeature1);
        } finally {
            if (faceEngine != null){
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 比对两个位图中最大两张脸的特征信息
     * @param bitmap 待比对位图
     * @param bitmap1 待比对位图
     * @return 位图中各最大的一张脸的特征相似度
     */
    @Nullable
    public Float compareFace(@NonNull Bitmap bitmap, @NonNull Bitmap bitmap1) throws Exception{
        RukFaceEngine faceEngine = null;
        try {
            //获取人脸引擎
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }

            // 获取第一张位图的人脸信息及特征信息
            List<FaceInfo> faceInfoList = faceEngine.detectFaces(bitmap);
            FaceInfo faceInfo;
            if (isListEmpty(faceInfoList) || (faceInfo = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("first bitmap detect faces list failure");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(bitmap, faceInfo);
            if (faceFeature == null){
                throw new FaceHandleException("first bitmap extract face feature failure");
            }

            // 获取第二张位图的人脸信息及特征信息
            List<FaceInfo> faceInfoList1 = detectFaces(bitmap1);
            FaceInfo faceInfo1;
            if (isListEmpty(faceInfoList1) || (faceInfo1 = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("second bitmap detect faces list failure");
            }
            FaceFeature faceFeature1 = faceEngine.extractFaceFeature(bitmap1, faceInfo1);
            if (faceFeature1 == null){
                throw new FaceHandleException("second bitmap extract face feature failure");
            }
            return faceEngine.compareFace(faceFeature, faceFeature1);
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 比对两个待比对的人脸特征相似度
     *
     * @param feature  待对比的人脸特征
     * @param feature1 待对比的人脸特征
     * @return 人脸特征相似度
     */
    @Nullable
    public Float compareFace(@NonNull FaceFeature feature, @NonNull FaceFeature feature1) throws Exception{
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
    public Float compareFace(@NonNull byte[] feature, @NonNull byte[] feature1) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            //获取人脸引擎
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }

            FaceFeature faceFeature = new FaceFeature();
            faceFeature.setFeatureData(feature);

            FaceFeature faceFeature1 = new FaceFeature();
            faceFeature1.setFeatureData(feature1);

            FaceSimilar faceSimilar = new FaceSimilar();
            int errorCode = faceEngine.compareFaceFeature(faceFeature, faceFeature1, faceSimilar);
            if (errorCode == ErrorInfo.MOK) {
                return faceSimilar.getScore();
            } else {
                Logger.e("compareFace failure, errorCode: " + errorCode);
            }
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
        return null;
    }

    /**
     * 从Bitmap位图画面数据中检测出的人脸列表
     *
     * @param bitmap 画面位图
     * @return 检测到的人脸列表
     */
    @Nullable
    public List<FaceInfo> detectFaces(@NonNull Bitmap bitmap) throws Exception{
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            return faceEngine.detectFaces(bitmap);
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 从NV21画面数据中检测出的人脸列表
     *
     * @param nv21Image nv21数据
     * @param width     nv21的视频宽度
     * @param height    nv21的视频高度
     * @return 检测到的人脸列表
     */
    @Nullable
    public List<FaceInfo> detectFaces(@NonNull byte[] nv21Image, int width, int height) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            return faceEngine.detectFaces(nv21Image, width, height);
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param bitmap 传入的图片数据
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeature extractFaceFeature(@NonNull Bitmap bitmap) throws Exception {
        FaceFeatureInfo faceFeatureInfo = extractFaceFeatureInfo(bitmap);
        FaceFeature faceFeature = new FaceFeature();
        faceFeature.setFeatureData(faceFeatureInfo.getFeatureData());
        return faceFeature;
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param bitmap 传入的图片数据
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeatureInfo extractFaceFeatureInfo(@NonNull Bitmap bitmap) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            FaceInfo faceInfo;
            List<FaceInfo> faceInfoList = faceEngine.detectFaces(bitmap);
            if (isListEmpty(faceInfoList) || (faceInfo = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("first bitmap detect faces list failure");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(bitmap, faceInfo);
            if (faceFeature != null){
                return new FaceFeatureInfo(faceInfo, faceFeature.getFeatureData());
            } else {
                throw new FaceHandleException("extract Face Feature failure");
            }
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param nv21 传入的图片数据
     * @param width 图片数据宽度
     * @param height 图片数据高度
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeature extractFaceFeature(@NonNull byte[] nv21, int width, int height) throws Exception {
        FaceFeatureInfo faceFeatureInfo = extractFaceFeatureInfo(nv21, width, height);
        FaceFeature faceFeature = new FaceFeature();
        faceFeature.setFeatureData(faceFeatureInfo.getFeatureData());
        return faceFeature;
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param nv21 传入的图片数据
     * @param width 图片数据宽度
     * @param height 图片数据高度
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeatureInfo extractFaceFeatureInfo(@NonNull byte[] nv21, int width, int height) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            FaceInfo faceInfo;
            List<FaceInfo> faceInfoList = faceEngine.detectFaces(nv21, width, height);
            if (isListEmpty(faceInfoList) || (faceInfo = firstListObject(faceInfoList)) == null) {
                throw new FaceHandleException("first bitmap detect faces list failure");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(nv21, width, height, faceInfo);
            if (faceFeature != null){
                return new FaceFeatureInfo(faceInfo, faceFeature.getFeatureData());
            } else {
                throw new FaceHandleException("extract Face Feature failure");
            }
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param bitmap 传入的图片数据
     * @param faceInfo 人脸在传入图片数据上的信息
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeatureInfo extractFaceFeatureInfo(@NonNull Bitmap bitmap, @NonNull FaceInfo faceInfo) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(bitmap, faceInfo);
            if (faceFeature != null){
                return new FaceFeatureInfo(faceInfo, faceFeature.getFeatureData());
            } else {
                throw new FaceHandleException("extract Face Feature failure");
            }
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 提取传入的图片数据中的最接近的一张人脸特征
     * @param nv21 传入的图片数据
     * @param width 图片数据宽度
     * @param height 图片数据高度
     * @param faceInfo 人脸在传入图片数据上的信息
     * @return 人脸特征信息
     * @throws Exception 处理中遇到的异常
     */
    @Nullable
    public FaceFeatureInfo extractFaceFeatureInfo(@NonNull byte[] nv21, int width, int height,
                                              @NonNull FaceInfo faceInfo) throws Exception {
        RukFaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null) {
                throw new FaceEngineException("fetch face engine failure!!!");
            }
            FaceFeature faceFeature = faceEngine.extractFaceFeature(nv21, width, height, faceInfo);
            if (faceFeature != null){
                return new FaceFeatureInfo(faceInfo, faceFeature.getFeatureData());
            } else {
                throw new FaceHandleException("extract Face Feature failure");
            }
        } finally {
            if (faceEngine != null) {
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 人脸特征信息转化人脸特征
     * @param featureInfo 人脸特征信息
     * @return 人脸特征
     */
    public FaceFeature convertFeature(@NonNull FaceFeatureInfo featureInfo){
        FaceFeature faceFeature = new FaceFeature();
        faceFeature.setFeatureData(featureInfo.getFeatureData());
        return faceFeature;
    }

    /**
     * 人脸特征信息, 该特征信息包含人脸信息
     */
    public static class FaceFeatureInfo extends FaceInfo{
        /**
         * 人脸特征
         */
        private byte[] featureData;

        public FaceFeatureInfo(Rect rect, int orient, int faceId, byte[] featureData) {
            super(rect, orient);
            setFaceId(faceId);
            this.featureData = featureData;
        }

        public FaceFeatureInfo(FaceInfo obj, byte[] featureData) {
            super(obj);
            this.featureData = featureData;
        }

        public byte[] getFeatureData() {
            return featureData;
        }

        @Override
        public String toString() {
            return "FaceFeatureInfo{" +
                    "featureData=" + Arrays.toString(featureData) +
                    '}';
        }
    }

    private FaceEngineService() {
    }

    private static class Holder {
        private static final FaceEngineService INSTANCE = new FaceEngineService();
    }

    private static boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }

    @Nullable
    private static <T> T firstListObject(List<T> list) {
        return isListEmpty(list) ? null : list.get(0);
    }
}
