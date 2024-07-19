package org.aoju.bus.ocr.loader;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.loader.Loaders;
import org.aoju.bus.health.Platform;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.ocr.Model;

import java.io.IOException;

/**
 * @author Justubborn
 * @since 2024/7/19
 */
public class RapidOcrNativeLoader implements LibraryLoader {
    public void init(Model model) {
        String path = Symbol.SLASH + Normal.LIB_PROTOCOL_JAR
                + Symbol.SLASH + Platform.getNativeLibraryResourcePrefix() + Symbol.SLASH + model.getModelType() + Symbol.SLASH
                + System.mapLibraryName("RapidOcr");
        try {
            Loaders.nat().load(path, RapidOcrNativeLoader.class);
            Logger.info("load the native Ocr library Success.");
        } catch (IOException e) {
            Logger.error(e,"Failed to load the native Ocr library.");
        }
       
    }
}
