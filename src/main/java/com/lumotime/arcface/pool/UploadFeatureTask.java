package com.lumotime.arcface.pool;

import com.google.common.collect.Lists;
import com.lumotime.arcface.data.UserFeatureInfo;
import com.lumotime.arcface.interf.ConvertFunction;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>文件名称: UploadFeatureTask </p>
 * <p>所属包名: com.lumotime.arcface.pool</p>
 * <p>描述: 上传人脸特征任务 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/30 13:41 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class UploadFeatureTask<T> implements Callable<List<UserFeatureInfo>> {

    /**
     * 待加入人脸库的实体数据列表
     */
    private List<T> entities;
    /**
     * 数据变换方法, 用于将实体 T 转化为 UserFeatureInfo
     */
    private ConvertFunction<T, UserFeatureInfo> convertFunction;

    public UploadFeatureTask(List<T> entities, ConvertFunction<T, UserFeatureInfo> convertFunction) {
        this.entities = entities;
        this.convertFunction = convertFunction;
    }

    @Override
    public List<UserFeatureInfo> call() throws Exception {
        if (convertFunction == null) {
            throw new IllegalArgumentException("Not found convert source entity " +
                    "to UserFeatureInfo Function converter.");
        }

        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("load face source entities list null or empty!!!");
        }

        List<UserFeatureInfo> resultUserFeatureInfo = Lists.newArrayList();
        for (int i = 0; i < entities.size(); i++) {
            T itemEntity = entities.get(i);
            UserFeatureInfo converted = convertFunction.convert(itemEntity);
            if (converted != null){
                resultUserFeatureInfo.add(converted);
            }
        }
        return resultUserFeatureInfo;
    }
}
