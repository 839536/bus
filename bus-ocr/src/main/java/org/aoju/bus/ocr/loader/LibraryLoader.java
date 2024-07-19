package org.aoju.bus.ocr.loader;

import org.aoju.bus.ocr.Model;

import java.io.IOException;

/**
 * @author Monster
 */
public interface LibraryLoader {

    void init(Model model) throws IOException;
}
