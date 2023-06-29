package org.example;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.libffidemo.NativeUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


public class JnaDemo {

    private static NativeInterface INSTANCE;

    static {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("JnaDemo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmpDir.toFile().deleteOnExit();
        final Path path = NativeUtil.exportLibrary(tmpDir, "libffidemo");

        INSTANCE = Native.load(path.toAbsolutePath().toString(), NativeInterface.class);
    }

    private static Memory formatPoint(String s) {
        Memory memory = new Memory(s.getBytes(StandardCharsets.UTF_8).length + 1);
        memory.setString(0, s);
        return memory;
    }

    public int nativeSum(int a, int b) {
        return INSTANCE.demo_sum(a, b);
    }

    public String nativeHello(String name) {
        try (Memory pname = formatPoint(name)) {
            final Pointer ps = INSTANCE.demo_generate_hello(pname);
            final String string = ps.getString(0, "UTF-8");
            INSTANCE.free(ps);
            return string;
        }
    }

    public Complex nativeGetById(int id) {
        Pointer ptr = INSTANCE.get_by_id(id);
        complex_t complex = new complex_t(ptr);
        final Complex c = complex.toComplex();
        complex.clear();
        INSTANCE.free(ptr);
        return c;
    }

    public Complex[] nativeGetByNameLike(String name) {
        try (Memory pname = formatPoint(name)) {
            Pointer ptrRef = INSTANCE.get_by_name_like(pname);
            Pointer[] ptrArray = ptrRef.getPointerArray(0);
            Complex[] result = new Complex[ptrArray.length];
            // Convert each pointer to complex_t object and access the fields
            for (int i = 0; i < ptrArray.length; i++) {
                final Pointer objPtr = ptrArray[i];
                complex_t complexItem = new complex_t(objPtr);
                // Access the fields of complexItem
                result[i] = complexItem.toComplex();
                complexItem.clear();
                INSTANCE.free(objPtr);
            }
            INSTANCE.free(ptrRef);
            return result;
        }
    }

    public interface NativeInterface extends Library {
        int demo_sum(int a, int b);

        Pointer demo_generate_hello(Pointer name);

        Pointer get_by_id(int id);

        Pointer get_by_name_like(Pointer name);

        void free(Pointer p);
    }


    public static class complex_t extends Structure {
        public int id;
        public Pointer name;
        public double score;

        public complex_t(Pointer ptr) {
            super(ptr);
            autoRead();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("id", "name", "score");
        }

        @Override
        public void clear() {
            super.clear();
            INSTANCE.free(name);
        }

        public Complex toComplex() {
            final Complex c = new Complex();
            c.setId(this.id);
            c.setName(this.name.getString(0, "utf8"));
            c.setScore(this.score);
            return c;
        }
    }

    public static void main(String[] args) {
        final JnaDemo demo = new JnaDemo();
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
