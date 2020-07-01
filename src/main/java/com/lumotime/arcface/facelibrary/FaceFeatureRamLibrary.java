package com.lumotime.arcface.facelibrary;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.lumotime.arcface.data.UserFeatureInfo;
import com.lumotime.arcface.interf.ConvertFunction;
import com.lumotime.arcface.pool.UploadFeatureTask;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.novakj.j3.core.util.FileUtils;
import cn.novakj.j3.core.util.SystemUtils;

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
     * 序列化到本地的文件名称
     */
    private static final String SERIALIZABLE_LOCAL_FILE_NAME = FaceFeatureRamLibrary.class.getSimpleName();

    /**
     * 储存人脸面部特征的数据库内存仓库
     */
    private ConcurrentHashMap<String, UserFeatureInfo> userFaceAccessInfoOfMap;

    private static volatile FaceFeatureRamLibrary instance;

    private FaceFeatureRamLibrary(){
        // 初始化内存人脸特征池
        userFaceAccessInfoOfMap = new ConcurrentHashMap<>();
    }

    public static FaceFeatureRamLibrary getInstance(){
        if (instance == null){
            synchronized (FaceFeatureRamLibrary.class){
                if (instance == null){
                    instance = new FaceFeatureRamLibrary();
                }
            }
        }
        return instance;
    }

    /**
     * 添加数据库到内存人脸面部特征数据库
     *
     * @param userFeatureInfo 用户人脸访问信息
     */
    public void addUser(@NonNull UserFeatureInfo userFeatureInfo) {
        userFaceAccessInfoOfMap.put(userFeatureInfo.getUserId(), userFeatureInfo);
    }

    /**
     * 添加数据库到内存人脸面部特征数据库
     *
     * @param userFeatureInfos 用户人脸访问信息
     */
    public long addUser(@NonNull List<UserFeatureInfo> userFeatureInfos) {
        long count = 0;
        for (int i = 0; i < userFeatureInfos.size(); i++) {
            UserFeatureInfo userFeatureInfo = userFeatureInfos.get(i);
            userFaceAccessInfoOfMap.put(userFeatureInfo.getUserId(), userFeatureInfo);
            count++;
        }
        return count;
    }

    /**
     * 获取用户访问控制信息列表
     *
     * @return 用户访问控制信息列表
     */
    public List<UserFeatureInfo> getUserAccessList() {
        return Lists.newArrayList(userFaceAccessInfoOfMap.values());
    }

    /**
     * 获取用户访问控制信息列表数量
     *
     * @return 列表数量
     */
    public int getUserAccessLibraryCount() {
        return userFaceAccessInfoOfMap.size();
    }

    /**
     * 清空用户人脸访问控制信息
     */
    public void clear() {
        userFaceAccessInfoOfMap.clear();
    }

    /**
     * 快速转换更新存储对象到人脸库中
     * @param entities 存储对象实体
     * @param groupSize 分组的大小
     * @param convertFunction 实体转化的工具类
     * @param <T> 实体的类型
     * @return 成功添加到人脸库的实体数量
     * @throws Exception 添加过程中遇到的异常
     */
    @SuppressWarnings("unchecked")
    public static <T> long fastUploadFeatureLibrary(
            List<T> entities, int groupSize,
            ConvertFunction<T, UserFeatureInfo> convertFunction
    ) throws Exception {
        if (groupSize <= 0)
            throw new IllegalArgumentException("entities group size > 0");
        //根据GroupSize 进行分组处理
        List<List<T>> faceUserInfoPartList = Lists.partition(entities, groupSize);
        //以机器CPU1.5倍的数量创建线程池, 用于快速的将数据转化合并
        ExecutorService convertExecutorService = Executors
                .newFixedThreadPool(Math.round(SystemUtils.getCpuCount() * 1.5F));
        CompletionService<List<UserFeatureInfo>> completionService
                = new ExecutorCompletionService(convertExecutorService);
        for (List<T> part : faceUserInfoPartList) {
            completionService.submit(new UploadFeatureTask<T>(part, convertFunction));
        }
        List<UserFeatureInfo> resultUserFeatureList = Lists.newLinkedList();
        // 合并转化后的结果
        for (int i = 0; i < faceUserInfoPartList.size(); i++) {
            List<UserFeatureInfo> faceUserFeatureInfo = null;
            try {
                faceUserFeatureInfo = completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.e(ex, "merge face recognition result failure");
            }finally {
                if (faceUserFeatureInfo != null && !faceUserFeatureInfo.isEmpty()) {
                    resultUserFeatureList.addAll(faceUserFeatureInfo);
                }
                Logger.d("merge face data size: " + resultUserFeatureList.size());
            }
        }
        // 将数据添加到人脸内存仓库中, 返回成功加入的结果
        return getInstance().addUser(resultUserFeatureList);
    }

    /**
     * 持久化内存中的人脸特征仓库
     * @throws IOException 序列化、持久化过程中遇到的异常
     */
    public void persistentLocalFacialFeatureWarehouse() throws IOException{
        this.persistentLocalFacialFeatureWarehouse(new File(
                FileUtils.getAppExternalFileDir("face_feature"), SERIALIZABLE_LOCAL_FILE_NAME
        ));
    }

    /**
     * 持久化内存中的人脸特征仓库
     * @param warehouse userFaceAccessInfoOfMap 存放的文件
     * @throws IOException 序列化、持久化过程中遇到的异常
     */
    private void persistentLocalFacialFeatureWarehouse(@NonNull File warehouse) throws IOException {
        // 判断文件文件是否存在, 请优先创建文件
        if (!FileUtils.isFileExists(warehouse)){
            boolean created = warehouse.createNewFile();
            if (!created){
                throw new FileNotFoundException("Not found Persistent Local " +
                        "Facial Feature Warehouse File in disk!");
            }
        }
        // 将userFaceAccessInfoOfMap人脸库序列化到本地
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(warehouse))) {
            oos.writeObject(userFaceAccessInfoOfMap);
        }
    }

    /**
     * 恢复序列化到本地的人脸特征仓库
     * @throws IOException 反序列化、反持久化中出现的异常
     */
    public void restoreLocalFacialFeatureWarehouse() throws IOException,
            ClassNotFoundException, ClassCastException{
        File faceFeatureFile = new File(
                FileUtils.getAppExternalFileDir("face_feature"), SERIALIZABLE_LOCAL_FILE_NAME
        );
        this.restoreLocalFacialFeatureWarehouse(faceFeatureFile);
    }

    /**
     * 恢复序列化到本地的人脸特征仓库
     * @param warehouse 序列化后仓库存放的文件
     * @throws IOException 反序列化、反持久化中出现的异常
     */
    public void restoreLocalFacialFeatureWarehouse(@NonNull File warehouse) throws IOException,
            ClassNotFoundException, ClassCastException{
        // 判断文件文件是否存在, 请优先创建文件
        if (!FileUtils.isFileExists(warehouse)){
            throw new FileNotFoundException("Not found Persistent Local " +
                    "Facial Feature Warehouse File in disk!");
        }
        // 读取本地中的序列化的人脸特征仓库
        ConcurrentHashMap<String, UserFeatureInfo> diskWareHouse;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(warehouse))) {
            diskWareHouse = (ConcurrentHashMap<String, UserFeatureInfo>) ois.readObject();
        }
        // 有可能本地的数据库有部分较新的数据存在, 所以先天际到硬盘读取出得对象中
        diskWareHouse.putAll(userFaceAccessInfoOfMap);
        // 清空当前人脸库数据, 重新将所有的人脸数据填充到当前人脸库中
        userFaceAccessInfoOfMap.clear();
        userFaceAccessInfoOfMap.putAll(diskWareHouse);
    }
}
