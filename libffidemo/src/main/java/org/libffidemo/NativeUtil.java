package org.libffidemo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 由于 jni 读取动态库时无法从 jar 中直接读取，需要一些机制才能实现，所以弄了个 util。
 * <p>
 * 这个 util 的方法是仿照 JNR-FFI StubLoader.java 做的
 */
public class NativeUtil {

    private static final boolean windows = System.getProperty("os.name").startsWith("Windows");
    private static final boolean osx = System.getProperty("os.name").startsWith("Mac");

    /**
     * 读取位于 classpath 的动态库
     *
     * @param outputDir 路径
     * @param libName   classpath 中库的名称，不包含 arch 和 .so 后缀
     */
    public static synchronized void loadClasspathLibrary(Path outputDir, String libName) {
        final Path tmpFile = exportLibrary(outputDir, libName);

        // load library from temp file
        System.load(tmpFile.toAbsolutePath().toString());
    }

    /**
     * 将 jar 包中的动态库输出到目录中
     *
     * @param outputDir 输出目录
     * @param libName   库名，不包含 .so 后缀
     * @return 输出路径
     */
    public static Path exportLibrary(Path outputDir, String libName) {
        final String suffix = getSuffix();
        final String path = libName + suffix;

        // 将 classpath 中的文件拿到流，然后输出到临时目录，系统关闭时删除库
        final InputStream in = NativeUtil.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalArgumentException("Cannot found classpath resource " + path);
        }
        final byte[] bytes;
        try {
            bytes = in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
            }
        }

        // write to temp file
        final Path tmpFile;
        try {
            tmpFile = outputDir.resolve(path);
            Files.write(tmpFile, bytes);
            tmpFile.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tmpFile;
    }

    private static String getSuffix() {
        return windows ? ".dll" : osx ? ".dylib" : ".so";
    }
}
