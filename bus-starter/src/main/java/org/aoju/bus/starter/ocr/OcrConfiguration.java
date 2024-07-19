package org.aoju.bus.starter.ocr;

import org.aoju.bus.ocr.InferenceEngine;
import org.aoju.bus.ocr.Model;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Justubborn
 * @since 2024/7/19
 */
@EnableConfigurationProperties(OcrProperties.class)
public class OcrConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public InferenceEngine engine(OcrProperties properties) {
        return InferenceEngine.getInstance(Model.ONNX_PPOCR_V4, properties.getCore());
    }
}
