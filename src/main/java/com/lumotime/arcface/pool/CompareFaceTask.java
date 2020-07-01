package com.lumotime.arcface.pool;

import com.arcsoft.face.FaceFeature;
import com.google.common.collect.Lists;
import com.lumotime.arcface.FaceEngineService;
import com.lumotime.arcface.data.UserFeatureInfo;
import com.lumotime.arcface.engine.RukFaceEngine;
import com.lumotime.arcface.exception.FaceEngineException;
import com.lumotime.arcface.exception.FaceHandleException;
import com.lumotime.arcface.facelibrary.FaceRecognition;
import com.orhanobut.logger.Logger;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>文件名称: CompareFaceTask </p>
 * <p>所属包名: com.lumotime.arcface.pool</p>
 * <p>描述: 人脸库比对任务 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/29 15:39 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class CompareFaceTask implements Callable<List<FaceRecognition.UserCompareInfo>> {

    /**
     * 人脸库特征数据列表
     */
    private List<UserFeatureInfo> userFeatureInfoList;
    /**
     * 1: N 待查找的人脸
     */
    private FaceFeature targetFaceFeature;
    /**
     * 人脸阈值
     */
    private float passRate;

    public CompareFaceTask(List<UserFeatureInfo> userFeatureInfoList, FaceFeature targetFaceFeature, float passRate) {
        this.userFeatureInfoList = userFeatureInfoList;
        this.targetFaceFeature = targetFaceFeature;
        this.passRate = passRate;
    }

    @Override
    public List<FaceRecognition.UserCompareInfo> call() throws Exception {
        RukFaceEngine faceEngine = null;
        GenericObjectPool<RukFaceEngine> faceEngineGeneralPool = FaceEngineService.getInstance()
                .getFaceEngineGeneralPool();
        //识别到的人脸列表
        List<FaceRecognition.UserCompareInfo> resultUserInfoList = Lists.newLinkedList();
        try {
            if (faceEngineGeneralPool == null){
                throw new FaceEngineException("fetch face engine pool failure");
            }
            faceEngine = faceEngineGeneralPool.borrowObject();
            if (faceEngine == null){
                throw new FaceEngineException("fetch face engine failure");
            }
            if (userFeatureInfoList == null || userFeatureInfoList.isEmpty()){
                throw new FaceHandleException("User Feature info list empty!!!");
            }
            if (targetFaceFeature == null){
                throw new FaceHandleException("Not found target face feature !!!");
            }

            FaceFeature faceFeature = new FaceFeature();
            for (UserFeatureInfo userFeatureInfo : userFeatureInfoList) {
                faceFeature.setFeatureData(userFeatureInfo.getFeature());
                Float faceSimilar = faceEngine.compareFace(faceFeature, targetFaceFeature);
                if (faceSimilar != null && faceSimilar > passRate){
                    FaceRecognition.UserCompareInfo userCompareInfo
                            = new FaceRecognition.UserCompareInfo(userFeatureInfo, faceSimilar);
                    // 将比对的结果添加到结果列表中
                    resultUserInfoList.add(userCompareInfo);
                }
            }
        }catch (Exception ex){
            Logger.e(ex, "Compare Face Task failure");
        } finally {
            if (faceEngine != null){
                faceEngineGeneralPool.returnObject(faceEngine);
            }
        }
        return resultUserInfoList;
    }
}