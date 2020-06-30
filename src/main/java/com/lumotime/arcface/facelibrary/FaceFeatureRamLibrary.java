package com.lumotime.arcface.facelibrary;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.lumotime.arcface.data.UserFeatureInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>文件名称: UserFaceAccessCache </p>
 * <p>所属包名: com.lumotime.arcface.facelibrary</p>
 * <p>描述: 用户人脸访问缓存器模块 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/28 14:20 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class FaceFeatureRamLibrary {

    /**
     * 储存人脸面部特征的数据库内存仓库
     */
    private static ConcurrentHashMap<String, UserFeatureInfo> userFaceAccessInfoOfMap
            = new ConcurrentHashMap<>();

    /**
     * 添加数据库到内存人脸面部特征数据库
     * @param userFeatureInfo 用户人脸访问信息
     */
    public static void addUser(@NonNull UserFeatureInfo userFeatureInfo){
        userFaceAccessInfoOfMap.put(userFeatureInfo.getUserId(), userFeatureInfo);
    }

    /**
     * 添加数据库到内存人脸面部特征数据库
     * @param userFeatureInfos 用户人脸访问信息
     */
    public static long addUser(@NonNull List<UserFeatureInfo> userFeatureInfos){
        long count = 0;
        for (int i = 0; i < userFeatureInfos.size(); i++) {
            UserFeatureInfo userFeatureInfo = userFeatureInfos.get(i);
            userFaceAccessInfoOfMap.put(userFeatureInfo.getUserId(), userFeatureInfo);
            count ++;
        }
        return count;
    }

    /**
     * 获取用户访问控制信息列表
     * @return 用户访问控制信息列表
     */
    public static List<UserFeatureInfo> getUserAccessList(){
        return Lists.newArrayList(userFaceAccessInfoOfMap.values());
    }

    /**
     * 获取用户访问控制信息列表数量
     * @return 列表数量
     */
    public static int getUserAccessLibraryCount(){
        return userFaceAccessInfoOfMap.size();
    }

    /**
     * 清空用户人脸访问控制信息
     */
    public static void clear(){
        userFaceAccessInfoOfMap.clear();
    }
}
