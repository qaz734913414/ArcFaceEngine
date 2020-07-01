package com.lumotime.arcface.data;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>文件名称: FaceFeatureInfo </p>
 * <p>所属包名: com.sdftgs.visitor.gate.data</p>
 * <p>描述: 人脸特征信息 </p>
 * <p>feature:
 *
 * </p>
 * <p>创建时间: 2020/6/23 11:53 </p>
 *
 * @author <a href="mail to: cnrivkaer@outlook.com" rel="nofollow">lumo</a>
 * @version v1.0
 */
public class UserFeatureInfo implements Cloneable, Serializable {

    private static final long serialVersionUID = 6161510305651808368L;

    private String userId;
    private String username;
    private String gender;
    private byte[] feature;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    @Nullable
    @Override
    public UserFeatureInfo clone() {
        UserFeatureInfo prototype = null;
        try{
            prototype = (UserFeatureInfo)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return prototype;
    }

    @Override
    public String toString() {
        return "UserFeatureInfo{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", feature=" + Arrays.toString(feature) +
                '}';
    }
}