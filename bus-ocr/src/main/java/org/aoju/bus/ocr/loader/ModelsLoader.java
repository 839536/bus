package org.aoju.bus.ocr.loader;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.ocr.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @author Monster
 */
public class ModelsLoader implements LibraryLoader {
   

    public static final String TEMP_DIR = new File(Objects.toString(System.getProperty("java.io.tmpdir")), "ocrJava").getPath();


    static File tempDir = null;

    @Override
    public void init(Model model) throws IOException {
        ModelsLoader.copyModelsFromJar(model, true);
        Logger.info("load the model Ocr model file Success.");
    }

    public static String getPath(Model model) {
        return TEMP_DIR + "/" + model.getModelType();
    }

    /**
     * @param filePath 原路径
     * @param aimDir 模型茉莉
     * @param deleteOnExit 退出时删除
     * @throws IOException io 异常
     */
    public static synchronized void copyFileFromJar(String filePath, String aimDir,  boolean deleteOnExit) throws IOException {

        // 获取文件名并校验
        String[] parts = filePath.split(Symbol.SLASH);
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
        if (filename == null || filename.length() < 3) {
            throw new IllegalArgumentException("文件名必须至少有3个字符长.");
        }
        // 检查目标文件夹是否存在
        if (tempDir == null) {
            tempDir = new File(TEMP_DIR, aimDir);
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new IOException("无法在临时目录创建文件夹" + tempDir);
            }
        }
        // 在临时文件夹下创建文件
        File temp = new File(tempDir, filename.startsWith(Symbol.SLASH) ? filename : Symbol.SLASH + filename);
        if (!temp.exists()) {
            // 从jar包中复制文件到系统临时文件夹
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
                if (is != null) {
                    Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    throw new NullPointerException();
                }
            } catch (IOException e) {
                Files.delete(temp.toPath());
                throw new IOException("无法复制文件 " + filePath + " 到 " + temp.getAbsolutePath(), e);
            } catch (NullPointerException e) {
                throw new FileNotFoundException("文件 " + filePath + " 在JAR中未找到.");
            }
        }
        // JVM结束时删除临时文件和临时文件夹
        if (deleteOnExit) {
            temp.deleteOnExit();
            tempDir.deleteOnExit();
        }
        Logger.debug("将文件{}复制到{}，JVM退出时删除此文件：{}", filePath, aimDir, deleteOnExit);
    }

    /**
     * 从jar包中复制models文件夹下的内容
     *
     * @param model 要使用的模型
     */
    public static void copyModelsFromJar(Model model, boolean isDelOnExit) throws IOException {
        String modelsDir = model.getModelsDir();
        String noStart = modelsDir.startsWith(Symbol.SLASH) ? modelsDir.substring(1) : modelsDir;
        String base = noStart.endsWith(Symbol.SLASH) ? noStart : noStart + Symbol.SLASH;
        for (final String path : model.getModelFileArray()) {
            copyFileFromJar(base + path, Symbol.SLASH + model.getModelType(),  isDelOnExit);
        }
    }
}
