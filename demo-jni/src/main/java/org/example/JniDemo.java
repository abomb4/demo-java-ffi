package org.example;

import org.libffidemo.NativeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class JniDemo {

    static {
        final Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("JniDemo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmpDir.toFile().deleteOnExit();
        NativeUtil.loadClasspathLibrary(tmpDir, "libJniDemo");
    }

    // Define native methods

    public native int nativeSum(int a, int b);

    public native String nativeHello(String name);

    public native Complex nativeGetById(int id);

    public native Complex[] nativeGetByNameLike(String name);

    public static void main(String[] args) {
        final JniDemo demo = new JniDemo();
        if (args.length >= 2 && "performance".equals(args[0])) {
            int seconds = Integer.parseInt(args[1]);
            final LocalDateTime end = LocalDateTime.now().plusSeconds(seconds);
            System.out.println("Run " + seconds + "s, ends at " + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            int count = 0;
            while (LocalDateTime.now().isBefore(end) && !Thread.currentThread().isInterrupted()) {
                demo.nativeSum(1234567, 7654321);
                demo.nativeHello("世界");
                demo.nativeGetById(1);
                demo.nativeGetByNameLike("张");
                count += 1;
            }
            final double tps = count / (double) seconds;
            System.out.printf("Run %d times in %ds, tps %f/s%n", count, seconds, tps);
        } else {
            System.out.println(demo.nativeSum(1234567, 7654321));
            System.out.println(demo.nativeHello("世界"));
            System.out.println(demo.nativeGetById(1));
            System.out.println(Arrays.toString(demo.nativeGetByNameLike("张")));
        }
    }
}
