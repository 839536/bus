package org.aoju.bus.starter.ocr;

import lombok.Data;
import org.aoju.bus.ocr.config.HardwareConfig;
import org.aoju.bus.spring.BusXConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Justubborn
 * @since 2024/7/19
 */
@Data
@ConfigurationProperties(BusXConfig.OCR)
public class OcrProperties {
    private HardwareConfig core = new HardwareConfig();

}
