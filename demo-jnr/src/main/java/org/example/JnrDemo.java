package org.example;


import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Encoding;
import org.libffidemo.NativeUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class JnrDemo {

    public static final int MAX_LENGTH = 33554432;
    private static NativeInterface INSTANCE;

    static {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("JnrDemo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmpDir.toFile().deleteOnExit();
        final Path path = NativeUtil.exportLibrary(tmpDir, "libffidemo");

        INSTANCE = LibraryLoader.create(NativeInterface.class).load(path.toAbsolutePath().toString());
    }

    public int nativeSum(int a, int b) {
        return INSTANCE.demo_sum(a, b);
    }

    public String nativeHello(String name) {
        final Pointer pointer = INSTANCE.demo_generate_hello(name);
        final String s = pointer.getString(0, MAX_LENGTH, StandardCharsets.UTF_8);
        INSTANCE.free(pointer);
        return s;
    }

    public Complex nativeGetById(int id) {
        complex_t complex = INSTANCE.get_by_id(id);
        final Complex c = complex.toComplex();
        complex.free();
        return c;
    }

    public Complex[] nativeGetByNameLike(String name) {
        final Pointer pointer = INSTANCE.get_by_name_like(name);
        final Pointer[] arr = pointer.getNullTerminatedPointerArray(0);
        Complex[] result = new Complex[arr.length];
        for (int i = 0; i < arr.length; i++) {
            final Pointer p = arr[i];
            final complex_t c = new complex_t(Runtime.getRuntime(INSTANCE));
            c.useMemory(p);
            result[i] = c.toComplex();
            c.free();
        }
        INSTANCE.free(pointer);
        return result;
    }

    public interface NativeInterface {
        int demo_sum(int a, int b);

        @Encoding("UTF-8")
        Pointer demo_generate_hello(String name);

        complex_t get_by_id(int id);

        @Encoding("UTF-8")
        Pointer get_by_name_like(String name);

        void free(Pointer p);
    }

    public static class complex_t extends Struct {
        private static final int MAX_LENGTH = JnrDemo.MAX_LENGTH;
        public final Struct.Signed32 id = new Struct.Signed32();
        public final Struct.Pointer name = new Struct.Pointer();
        public final Struct.Double score = new Struct.Double();

        public complex_t(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public Complex toComplex() {
            final Complex c = new Complex();
            c.setId(this.id.get());
            c.setName(this.name.get().getString(0, MAX_LENGTH, StandardCharsets.UTF_8));
            c.setScore(this.score.get());
            return c;
        }

        public void free() {
            INSTANCE.free(name.get());
            INSTANCE.free(getMemory(this));
        }
    }

    public static void main(String[] args) {
        final JnrDemo demo = new JnrDemo();
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
