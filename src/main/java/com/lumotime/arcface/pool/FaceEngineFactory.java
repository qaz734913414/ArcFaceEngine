package com.lumotime.arcface.pool;

import android.content.Context;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.lumotime.arcface.config.Config;
import com.lumotime.arcface.config.EngineConfiguration;
import com.lumotime.arcface.engine.RukFaceEngine;
import com.lumotime.arcface.exception.FaceEngineException;
import com.orhanobut.logger.Logger;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import cn.novakj.j3.core.Utils;

public class FaceEngineFactory extends BasePooledObjectFactory<RukFaceEngine> {

    private Context context;
    private String appId;
    private String sdkKey;
    private String activeKey;
    private EngineConfiguration engineConfiguration;

    public FaceEngineFactory(Context context, String appId, String sdkKey, EngineConfiguration engineConfiguration) {
        this.context = context;
        this.appId = appId;
        this.sdkKey = sdkKey;
        this.engineConfiguration = engineConfiguration;
    }

    @Override
    public RukFaceEngine create() {
        RukFaceEngine faceEngine = new RukFaceEngine();
        // 在线激活当前的引擎, 使用本应用必须进行一次联网激活
        int activeCode = RukFaceEngine.activeOnline(context, appId, sdkKey);
        if (activeCode != ErrorInfo.MOK && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            Logger.e("引擎激活失败" + activeCode);
            throw new FaceEngineException("引擎激活失败" + activeCode);
        }
        int initCode = faceEngine.init(context, engineConfiguration);
        if (initCode != ErrorInfo.MOK) {
            Logger.e("引擎初始化失败" + initCode);
            throw new FaceEngineException("引擎初始化失败" + initCode);
        }
        return faceEngine;
    }

    @Override
    public PooledObject<RukFaceEngine> wrap(RukFaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }

    @Override
    public void destroyObject(PooledObject<RukFaceEngine> p) throws Exception {
        RukFaceEngine faceEngine = p.getObject();
        int result = faceEngine.unInit();
        super.destroyObject(p);
    }
}