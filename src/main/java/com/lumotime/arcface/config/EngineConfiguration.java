package com.lumotime.arcface.config;

import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;

public class EngineConfiguration {
    private FunctionConfiguration functionConfiguration;
    private Integer detectFaceMaxNum;
    private Integer detectFaceScaleVal;
    private DetectMode detectMode;
    private DetectFaceOrientPriority detectFaceOrientPriority;

    public FunctionConfiguration getFunctionConfiguration() {
        return this.functionConfiguration;
    }

    public void setFunctionConfiguration(FunctionConfiguration functionConfiguration) {
        this.functionConfiguration = functionConfiguration;
    }

    public Integer getDetectFaceMaxNum() {
        return this.detectFaceMaxNum;
    }

    public void setDetectFaceMaxNum(Integer detectFaceMaxNum) {
        this.detectFaceMaxNum = detectFaceMaxNum;
    }

    public Integer getDetectFaceScaleVal() {
        return this.detectFaceScaleVal;
    }

    public void setDetectFaceScaleVal(Integer detectFaceScaleVal) {
        this.detectFaceScaleVal = detectFaceScaleVal;
    }

    public DetectMode getDetectMode() {
        return this.detectMode;
    }

    public void setDetectMode(DetectMode detectMode) {
        this.detectMode = detectMode;
    }

    public DetectFaceOrientPriority getDetectFaceOrientPriority() {
        return detectFaceOrientPriority;
    }

    public void setDetectFaceOrientPriority(DetectFaceOrientPriority detectFaceOrientPriority) {
        this.detectFaceOrientPriority = detectFaceOrientPriority;
    }

    public EngineConfiguration() {
        this.functionConfiguration = FunctionConfiguration.builder().build();
        this.detectFaceMaxNum = 10;
        this.detectFaceScaleVal = 16;
        this.detectMode = DetectMode.ASF_DETECT_MODE_IMAGE;
        this.detectFaceOrientPriority = DetectFaceOrientPriority.ASF_OP_0_ONLY;
    }

    private EngineConfiguration(EngineConfiguration.Builder builder) {
        this.functionConfiguration = FunctionConfiguration.builder().build();
        this.detectFaceMaxNum = 10;
        this.detectFaceScaleVal = 16;
        this.detectMode = DetectMode.ASF_DETECT_MODE_IMAGE;
        this.detectFaceOrientPriority = DetectFaceOrientPriority.ASF_OP_0_ONLY;
        this.functionConfiguration = builder.functionConfiguration;
        this.detectFaceMaxNum = builder.detectFaceMaxNum;
        this.detectFaceScaleVal = builder.detectFaceScaleVal;
        this.detectMode = builder.detectMode;
        this.detectFaceOrientPriority = builder.detectFaceOrientPriority;
    }

    public static EngineConfiguration.Builder builder() {
        return new EngineConfiguration.Builder();
    }

    public static final class Builder {
        private FunctionConfiguration functionConfiguration;
        private Integer detectFaceMaxNum;
        private Integer detectFaceScaleVal;
        private DetectMode detectMode;
        private DetectFaceOrientPriority detectFaceOrientPriority;

        private Builder() {
            this.functionConfiguration = FunctionConfiguration.builder().build();
            this.detectFaceMaxNum = 10;
            this.detectFaceScaleVal = 16;
            this.detectMode = DetectMode.ASF_DETECT_MODE_IMAGE;
            this.detectFaceOrientPriority = DetectFaceOrientPriority.ASF_OP_0_ONLY;
        }

        public EngineConfiguration build() {
            return new EngineConfiguration(this);
        }

        public EngineConfiguration.Builder functionConfiguration(FunctionConfiguration functionConfiguration) {
            this.functionConfiguration = functionConfiguration;
            return this;
        }

        public EngineConfiguration.Builder detectFaceMaxNum(Integer detectFaceMaxNum) {
            this.detectFaceMaxNum = detectFaceMaxNum;
            return this;
        }

        public EngineConfiguration.Builder detectFaceScaleVal(Integer detectFaceScaleVal) {
            this.detectFaceScaleVal = detectFaceScaleVal;
            return this;
        }

        public EngineConfiguration.Builder detectMode(DetectMode detectMode) {
            this.detectMode = detectMode;
            return this;
        }

        public EngineConfiguration.Builder detectFaceOrientPriority(DetectFaceOrientPriority detectFaceOrientPriority) {
            this.detectFaceOrientPriority = detectFaceOrientPriority;
            return this;
        }
    }
}
