package org.libffidemo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 这个类是方便 so/dll 使用而准备的。
 * <p>
 * 如果没有这个类的机制，必须把编译好的 dll 或 so 放在 PATH 或 LD_LIBRARY_PATH 中能找到的位置，比较麻烦
 */
public class LibLoader {
    public static Path tmpDir;

    /**
     * 读取位于 classpath 的动态库
     */
    public static void load() {
        try {
            tmpDir = Files.createTempDirectory("libffidemo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmpDir.toFile().deleteOnExit();
        NativeUtil.loadClasspathLibrary(tmpDir, "libffidemo");
    }
}
