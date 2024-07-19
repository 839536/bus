package org.aoju.bus.ocr;


import com.benjaminwan.ocrlibrary.OcrEngine;
import com.benjaminwan.ocrlibrary.OcrInput;
import com.benjaminwan.ocrlibrary.OcrResult;
import lombok.Getter;
import lombok.SneakyThrows;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.ocr.config.HardwareConfig;
import org.aoju.bus.ocr.config.ParamConfig;
import org.aoju.bus.ocr.loader.LibraryLoader;
import org.aoju.bus.ocr.loader.ModelsLoader;
import org.aoju.bus.ocr.loader.RapidOcrNativeLoader;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inference framework engine.
 */

@Getter
public class InferenceEngine extends OcrEngine {
    private volatile boolean isInit = false;
    private String inferType;
    private Model model = Model.ONNX_PPOCR_V3;
    private HardwareConfig hardwareConfig;
    private static InferenceEngine inferenceEngine;
    private static volatile LibraryLoader nativeLoader;
    private static volatile LibraryLoader modelsLoader;
    private static final AtomicBoolean isLibraryLoaded = new AtomicBoolean(false);

    private InferenceEngine() {
    }

    private InferenceEngine(Model model, HardwareConfig hardwareConfig) {
        this.model = model;
        this.hardwareConfig = hardwareConfig;
        loadFileIfNeeded(model);
        initEngine(model, hardwareConfig);
    }

    public static InferenceEngine getInstance(Model model) {
        HardwareConfig hardwareConfig;
        if ("onnx".equals(model.getModelType())) {
            hardwareConfig = HardwareConfig.getOnnxConfig();
        } else {
            hardwareConfig = HardwareConfig.getNcnnConfig();
        }
        return getInstance(model, hardwareConfig);
    }

    public static InferenceEngine getInstance(Model model, HardwareConfig hardwareConfig) {
        if (inferenceEngine == null) {
            inferenceEngine = new InferenceEngine(model, hardwareConfig);
        }
        return inferenceEngine;
    }

    public OcrResult runOcr(String imagePath) {
        return runOcr(imagePath, ParamConfig.getDefaultConfig());
    }

    public OcrResult runOcr(String imagePath, ParamConfig config) {
        Logger.info("Image path: {}, Parameter configuration: {}", imagePath, config);
        OcrResult result = detect(imagePath, config.getPadding(), config.getMaxSideLen(), config.getBoxScoreThresh(), config.getBoxThresh(), config.getUnClipRatio(), config.isDoAngle(), config.isMostAngle());
        String property = System.getProperty("rapid.ocr.print.result");
        if ("true".equals(property)) {
            Logger.info("Recognition result: {}, Time taken: {}ms", result.getStrRes().replace("\n", ""), result.getDetectTime());
        }
        Logger.debug("Text blocks: {}, DbNet Time taken: {}ms", result.getTextBlocks(), result.getDbNetTime());
        return result;
    }

    public OcrResult runOcr(OcrInput input, ParamConfig config) {
        Logger.info("Image path: {}, Parameter configuration: {}", input.getData().length, config);
        OcrResult result = detectInput(input, config.getPadding(), config.getMaxSideLen(), config.getBoxScoreThresh(), config.getBoxThresh(), config.getUnClipRatio(), config.isDoAngle(), config.isMostAngle());
        String property = System.getProperty("rapid.ocr.print.result");
        if ("true".equals(property)) {
            Logger.info("Recognition result: {}, Time taken: {}ms", result.getStrRes().replace("\n", ""), result.getDetectTime());
        }
        Logger.debug("Text blocks: {}, DbNet Time taken: {}ms", result.getTextBlocks(), result.getDbNetTime());
        return result;
    }

    @SneakyThrows
    public static void loadFileIfNeeded(Model model) {
        if (InferenceEngine.nativeLoader == null && (isLibraryLoaded.compareAndSet(false, true))) {
            synchronized (InferenceEngine.class) {
                if (InferenceEngine.nativeLoader == null) {
                    LibraryLoader loader = new RapidOcrNativeLoader();
                    loader.init(model);
                    isLibraryLoaded.set(true);
                    InferenceEngine.nativeLoader = loader;
                }
            }
        }
        if (InferenceEngine.modelsLoader == null) {
            synchronized (InferenceEngine.class) {
                if (InferenceEngine.modelsLoader == null) {
                    LibraryLoader loader = new ModelsLoader();
                    loader.init(model);
                    InferenceEngine.modelsLoader = loader;
                }
            }
        }
    }

    public void initEngine(Model model, HardwareConfig hardwareConfig) {
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {
                    initLogger(false, false, false);
                    setNumThread(hardwareConfig.getNumThread());
                    if (hardwareConfig.getGpuIndex() != -1) {
                        setGpuIndex(hardwareConfig.getGpuIndex());
                    }
                    if (!initModels(ModelsLoader.getPath(model), model.getDetName(), model.getClsName(), model.getRecName(), model.getKeysName())) {
                        Logger.error("Model initialization error, please check the models path! Model: {}", model);
                        throw new IllegalArgumentException("Model initialization error, please check the models/keys path!");
                    }
                    inferType = model.getModelType();
                    Logger.info("Inference engine initialized successfully, currently using inference engine: {}-v{}", inferType, getVersion());
                    Logger.info("Model configuration during initialization: {}, Hardware configuration: {}", model, hardwareConfig);
                    isInit = true;
                }
            }
            Logger.info("Rapid Ocr Version : {}", getVersion());
        } else {
            if (!Objects.equals(model.getModelType(), inferType)) {
                Logger.warn("Engine has been initialized already; switching engines post-initialization is not supported, continuing to use {} inference engine", inferType);
            } else {
                Logger.info("Currently using inference engine: {}-v{}", inferType, getVersion());
            }
        }
    }
}
