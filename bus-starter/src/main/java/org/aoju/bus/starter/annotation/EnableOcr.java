package org.aoju.bus.starter.annotation;

import org.aoju.bus.starter.ocr.OcrConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动ocr
 * @author Justubborn
 * @since 2024/7/19
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({OcrConfiguration.class})
public @interface EnableOcr {
}
