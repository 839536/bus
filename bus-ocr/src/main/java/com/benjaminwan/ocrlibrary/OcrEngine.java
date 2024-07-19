package com.benjaminwan.ocrlibrary;

/**
 * OCR engine object that interacts with library files through JNI.
 */
public class OcrEngine {

 
    public native boolean setNumThread(int numThread);

    public native void initLogger(boolean isConsole, boolean isPartImg, boolean isResultImg);

    public native void enableResultText(String imagePath);

    public native boolean initModels(String modelsDir, String detName, String clsName, String recName, String keysName);

    public native void setGpuIndex(int gpuIndex);

    public native String getVersion();
    
    public native OcrResult detect(
        String input, int padding, int maxSideLen,
        float boxScoreThresh, float boxThresh,
        float unClipRatio, boolean doAngle, boolean mostAngle
    );

    public native OcrResult detectInput(
        OcrInput input, int padding, int maxSideLen,
        float boxScoreThresh, float boxThresh,
        float unClipRatio, boolean doAngle, boolean mostAngle
    );

}
