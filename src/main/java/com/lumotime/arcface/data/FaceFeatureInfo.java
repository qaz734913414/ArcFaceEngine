package com.lumotime.arcface.data;

import android.graphics.Rect;

import com.arcsoft.face.FaceInfo;

import java.util.Arrays;

/**
 * 人脸特征信息, 该特征信息包含人脸信息
 */
public class FaceFeatureInfo extends FaceInfo {
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