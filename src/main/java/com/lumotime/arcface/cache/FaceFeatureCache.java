package com.lumotime.arcface.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lumotime.arcface.data.UserFeatureInfo;
import com.lumotime.arcface.facelibrary.FaceFeatureRamLibrary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * <p>文件名称: FaceFeatureCache </p>
 * <p>所属包名: com.lumotime.arcface.cache</p>
 * <p>描述: 人脸特征缓存器 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/30 17:16 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceFeatureCache {

    private static final int DEFAULT_CONTAINER_OMIT_SIZE = 50;
    public static final int DEFAULT_FACE_FEATURE_RAM_LIBRARY_CACHE_INIT_SIZE = 1000;

    /**
     * 缓存器配置
     */
    private FaceFeatureCacheConfig cacheConfig;

    /**
     * 一个用于记录系统检查人脸历史的定长队列, 该队列仅在检测{@link FaceFeatureRamLibrary}中的特征数据时
     * 才可以生效,
     */
    private LinkedHashMap<String, UserFeatureInfo> mSystemRecognitionHistory;

    public FaceFeatureCache(@NonNull FaceFeatureCacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        mSystemRecognitionHistory = Maps.newLinkedHashMapWithExpectedSize(
                cacheConfig.getContainerSize()
        );
    }

    /**
     * 向缓存器中添加一个特征
     * @param userFeatureInfo 特征信息
     */
    public synchronized void addCache(@NonNull UserFeatureInfo userFeatureInfo){
        checkContainerFullAndClearFullOldCacheElement();
        if (cacheConfig.isCacheUse()){
            mSystemRecognitionHistory.put(userFeatureInfo.getUserId(), userFeatureInfo);
        }
    }

    /**
     * 从缓存中获取指定的特征
     * @param cacheName 特征名称
     * @return 指定的特征
     */
    @Nullable
    public synchronized UserFeatureInfo getCache(@NonNull String cacheName){
        return mSystemRecognitionHistory.get(cacheName);
    }

    /**
     * 获取所有的特征缓存
     * @return 所有的特征缓存
     */
    public synchronized LinkedList<UserFeatureInfo> getAllCache(){
        return Lists.newLinkedList(mSystemRecognitionHistory.values());
    }

    /**
     * 获取当前缓存器中元素数目
     * @return 缓存器中元素数目
     */
    public synchronized int getCount(){
        return mSystemRecognitionHistory.size();
    }

    /**
     * 清空缓存容器
     */
    public synchronized void clear(){
        mSystemRecognitionHistory.clear();
    }

    public FaceFeatureCacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCacheConfig(FaceFeatureCacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    /**
     * 检查容器已满并清除已满的旧缓存元素
     */
    private void checkContainerFullAndClearFullOldCacheElement(){
        if (mSystemRecognitionHistory.size() + DEFAULT_CONTAINER_OMIT_SIZE
                < cacheConfig.getContainerSize()){
            return;
        }
        int oldCacheElementLength = mSystemRecognitionHistory.size() / 4 * 3;
        Set<String> keys = mSystemRecognitionHistory.keySet();
        Iterator<Map.Entry<String, UserFeatureInfo>> iterator = mSystemRecognitionHistory
                .entrySet().iterator();
        int removeTotal = 0;
        while (iterator.hasNext()){
            iterator.next();
            if (removeTotal < oldCacheElementLength){
                iterator.remove();
            } else {
                break;
            }
            oldCacheElementLength ++;
        }
    }
}
