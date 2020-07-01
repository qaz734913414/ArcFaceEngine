package com.lumotime.arcface.cache;

import com.lumotime.arcface.facelibrary.FaceFeatureRamLibrary;

/**
 * <p>文件名称: FaceFeatureCacheConfig </p>
 * <p>所属包名: com.lumotime.arcface.cache</p>
 * <p>描述: 人脸特征缓存器配置 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/30 17:17 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceFeatureCacheConfig {
    /**
     * 是否使用缓存器
     */
    private boolean cacheUse;
    /**
     * 合格率 该结果不同于人脸比对阀值, 应大于人脸比对阀值, 因为该结果比对的为缓冲结果
     * 数据: [0, 1]
     */
    private float passRate;
    /**
     * 初始化容器的大小
     */
    private int containerSize;

    private FaceFeatureCacheConfig(){}

    public boolean isCacheUse() {
        return cacheUse;
    }

    public int getContainerSize() {
        return containerSize;
    }

    public float getPassRate() {
        return passRate;
    }

    @Override
    public String toString() {
        return "FaceFeatureCacheConfig{" +
                "cacheUse=" + cacheUse +
                ", containerSize=" + containerSize +
                '}';
    }

    public static class Builder{
        private boolean cacheUse = false;
        private int containerSize = FaceFeatureCache
                .DEFAULT_FACE_FEATURE_RAM_LIBRARY_CACHE_INIT_SIZE;
        private float passRate = 0.95f;

        public boolean isCacheUse() {
            return cacheUse;
        }

        public Builder setCacheUse(boolean cacheUse) {
            this.cacheUse = cacheUse;
            return this;
        }

        public int getContainerSize() {
            return containerSize;
        }

        public float getPassRate() {
            return passRate;
        }

        public void setPassRate(float passRate) {
            this.passRate = passRate;
        }

        public FaceFeatureCacheConfig build(){
            FaceFeatureCacheConfig faceFeatureCacheConfig = new FaceFeatureCacheConfig();
            faceFeatureCacheConfig.cacheUse = this.cacheUse;
            faceFeatureCacheConfig.containerSize = this.containerSize;
            faceFeatureCacheConfig.passRate = this.passRate;
            return faceFeatureCacheConfig;
        }
    }
}
