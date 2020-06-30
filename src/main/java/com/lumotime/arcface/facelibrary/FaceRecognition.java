package com.lumotime.arcface.facelibrary;

import com.arcsoft.face.FaceFeature;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.lumotime.arcface.data.UserFeatureInfo;
import com.lumotime.arcface.pool.CompareFaceTask;
import com.orhanobut.logger.Logger;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import cn.novakj.j3.core.util.SystemUtils;

/**
 * <p>文件名称: FaceRecognition </p>
 * <p>所属包名: com.lumotime.arcface.facelibrary</p>
 * <p>描述: 人脸识别 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/29 15:23 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceRecognition {

    /**
     * 查找比对人脸库的线程池
     */
    private ExecutorService compareExecutorService;

    /**
     * 人脸库 人脸快速识别
     * @param faceFeature 人脸特征
     * @param userCompares 人脸特征库列表
     * @param passRate 结果阈值
     * @return 人脸比对最接近的结果
     */
    public List<UserCompareInfo> faceRecognition(
            FaceFeature faceFeature,
            List<UserFeatureInfo> userCompares,
            float passRate) {
        //识别到的人脸列表
        List<UserCompareInfo> resultUserInfoList = Lists.newLinkedList();
        //分成1000一组，多线程处理
        List<List<UserFeatureInfo>> faceUserInfoPartList = Lists.partition(userCompares, 1000);
        CompletionService<List<UserCompareInfo>> completionService
                = new ExecutorCompletionService(compareExecutorService);
        for (List<UserFeatureInfo> part : faceUserInfoPartList) {
            completionService.submit(new CompareFaceTask(part, faceFeature, passRate));
        }
        for (int i = 0; i < faceUserInfoPartList.size(); i++) {
            List<UserCompareInfo> faceUserInfoList = null;
            try {
                faceUserInfoList = completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.e(ex, "merge face recognition result failure");
            }finally {
                if (faceUserInfoList != null && !faceUserInfoList.isEmpty()) {
                    resultUserInfoList.addAll(faceUserInfoList);
                }
            }
        }

        // 按照特征的相似程度进行排序
        return new Ordering<UserCompareInfo>() {
            @Override
            public int compare(@NullableDecl UserCompareInfo left, @NullableDecl UserCompareInfo right) {
                Float leftSimilar = 0F;
                if (left != null && left.getSimilar() != null){
                    leftSimilar = left.getSimilar();
                }
                Float rightSimilar = 0F;
                if (right != null && right.getSimilar() != null){
                    rightSimilar = right.getSimilar();
                }
                return rightSimilar.compareTo(leftSimilar);
            }
        }.immutableSortedCopy(resultUserInfoList);
    }

    /**
     * 人脸比中信息结果
     */
    public static class UserCompareInfo extends UserFeatureInfo {
        /**
         * 人脸特征相似度
         */
        private Float similar;

        public UserCompareInfo() {
        }

        public UserCompareInfo(UserFeatureInfo userFeatureInfo, Float similar) {
            this.similar = similar;
            setUserId(userFeatureInfo.getUserId());
            setUsername(userFeatureInfo.getUsername());
            setGender(userFeatureInfo.getGender());
            setFeature(userFeatureInfo.getFeature());
        }

        public Float getSimilar() {
            return similar;
        }

        public void setSimilar(Float similar) {
            this.similar = similar;
        }

        @Override
        public String toString() {
            return "FaceCompareInfo{" +
                    "similar=" + similar +
                    '}';
        }
    }

    private FaceRecognition() {
        // 人像的对比查找数据 计算密集型 操作, 线程池核心线程数 == Cpu核心数
        compareExecutorService = Executors.newFixedThreadPool(SystemUtils.getCpuCount());
    }

    private static class Holder {
        private static final FaceRecognition INSTANCE = new FaceRecognition();
    }

    public static FaceRecognition getInstance() {
        return Holder.INSTANCE;
    }
}
