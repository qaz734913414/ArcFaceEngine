//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lumotime.arcface.config;

public class FunctionConfiguration {
    private Boolean supportFaceDetect;
    private Boolean supportFaceRecognition;
    private Boolean supportAge;
    private Boolean supportGender;
    private Boolean supportFace3dAngle;
    private Boolean supportLiveness;
    private Boolean supportIRLiveness;

    public Boolean isSupportFaceDetect() {
        return this.supportFaceDetect;
    }

    public void setSupportFaceDetect(Boolean supportFaceDetect) {
        this.supportFaceDetect = supportFaceDetect;
    }

    public Boolean isSupportFaceRecognition() {
        return this.supportFaceRecognition;
    }

    public void setSupportFaceRecognition(Boolean supportFaceRecognition) {
        this.supportFaceRecognition = supportFaceRecognition;
    }

    public Boolean isSupportAge() {
        return this.supportAge;
    }

    public void setSupportAge(Boolean supportAge) {
        this.supportAge = supportAge;
    }

    public Boolean isSupportGender() {
        return this.supportGender;
    }

    public void setSupportGender(Boolean supportGender) {
        this.supportGender = supportGender;
    }

    public Boolean isSupportFace3dAngle() {
        return this.supportFace3dAngle;
    }

    public void setSupportFace3dAngle(Boolean supportFace3dAngle) {
        this.supportFace3dAngle = supportFace3dAngle;
    }

    public Boolean isSupportLiveness() {
        return this.supportLiveness;
    }

    public void setSupportLiveness(Boolean supportLiveness) {
        this.supportLiveness = supportLiveness;
    }

    public Boolean isSupportIRLiveness() {
        return this.supportIRLiveness;
    }

    public void setSupportIRLiveness(Boolean supportIRLiveness) {
        this.supportIRLiveness = supportIRLiveness;
    }

    public FunctionConfiguration() {
        this.supportFaceDetect = false;
        this.supportFaceRecognition = false;
        this.supportAge = false;
        this.supportGender = false;
        this.supportFace3dAngle = false;
        this.supportLiveness = false;
        this.supportIRLiveness = false;
    }

    private FunctionConfiguration(FunctionConfiguration.Builder builder) {
        this.supportFaceDetect = false;
        this.supportFaceRecognition = false;
        this.supportAge = false;
        this.supportGender = false;
        this.supportFace3dAngle = false;
        this.supportLiveness = false;
        this.supportIRLiveness = false;
        this.supportFaceDetect = builder.supportFaceDetect;
        this.supportFaceRecognition = builder.supportFaceRecognition;
        this.supportAge = builder.supportAge;
        this.supportGender = builder.supportGender;
        this.supportFace3dAngle = builder.supportFace3dAngle;
        this.supportLiveness = builder.supportLiveness;
        this.supportIRLiveness = builder.supportIRLiveness;
    }

    public static FunctionConfiguration.Builder builder() {
        return new FunctionConfiguration.Builder();
    }

    public static final class Builder {
        private Boolean supportFaceDetect;
        private Boolean supportFaceRecognition;
        private Boolean supportAge;
        private Boolean supportGender;
        private Boolean supportFace3dAngle;
        private Boolean supportLiveness;
        private Boolean supportIRLiveness;

        private Builder() {
            this.supportFaceDetect = false;
            this.supportFaceRecognition = false;
            this.supportAge = false;
            this.supportGender = false;
            this.supportFace3dAngle = false;
            this.supportLiveness = false;
            this.supportIRLiveness = false;
        }

        public FunctionConfiguration build() {
            return new FunctionConfiguration(this);
        }

        public FunctionConfiguration.Builder supportFaceDetect(Boolean supportFaceDetect) {
            this.supportFaceDetect = supportFaceDetect;
            return this;
        }

        public FunctionConfiguration.Builder supportFaceRecognition(Boolean supportFaceRecognition) {
            this.supportFaceRecognition = supportFaceRecognition;
            return this;
        }

        public FunctionConfiguration.Builder supportAge(Boolean supportAge) {
            this.supportAge = supportAge;
            return this;
        }

        public FunctionConfiguration.Builder supportGender(Boolean supportGender) {
            this.supportGender = supportGender;
            return this;
        }

        public FunctionConfiguration.Builder supportFace3dAngle(Boolean supportFace3dAngle) {
            this.supportFace3dAngle = supportFace3dAngle;
            return this;
        }

        public FunctionConfiguration.Builder supportLiveness(Boolean supportLiveness) {
            this.supportLiveness = supportLiveness;
            return this;
        }

        public FunctionConfiguration.Builder supportIRLiveness(Boolean supportIRLiveness) {
            this.supportIRLiveness = supportIRLiveness;
            return this;
        }
    }
}
