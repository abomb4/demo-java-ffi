package org.example;

import lombok.SneakyThrows;
import org.example.panama.complex_t;
import org.example.panama.libffidemo_h;
import org.libffidemo.NativeUtil;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.lang.foreign.ValueLayout.ADDRESS;

public class PanamaDemo {

    public static final Linker LINKER = Linker.nativeLinker();
    static final MethodHandle free = LINKER.downcallHandle(
            LINKER.defaultLookup().find("free").orElseThrow(UnknownError::new),
            FunctionDescriptor.ofVoid(ADDRESS)
    );

    static {
        Path tmpDir;
        try {
            tmpDir = Files.createTempDirectory("PanamaDemo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmpDir.toFile().deleteOnExit();
        NativeUtil.loadClasspathLibrary(tmpDir, "libffidemo");
    }

    public int nativeSum(int a, int b) {
        return libffidemo_h.demo_sum(a, b);
    }

    public String nativeHello(String name) {
        try (final Arena arena = Arena.openConfined()) {
            final MemorySegment req = arena.allocateUtf8String(name);
            final MemorySegment resp = libffidemo_h.demo_generate_hello(req);
            final String s = resp.getUtf8String(0);
            free(resp);
            return s;
        }
    }

    public Complex nativeGetById(int id) {
        final MemorySegment seg = libffidemo_h.get_by_id(id);
        final Complex c = new Complex();
        c.setId(complex_t.id$get(seg));
        final MemorySegment nameSeg = complex_t.name$get(seg);
        c.setName(nameSeg.getUtf8String(0));
        c.setScore(complex_t.score$get(seg));
        free(nameSeg);
        free(seg);
        return c;
    }

    public Complex[] nativeGetByNameLike(String name) {
        try (final Arena arena = Arena.openConfined()) {
            final MemorySegment req = arena.allocateUtf8String(name);
            final MemorySegment seg = libffidemo_h.get_by_name_like(req);

            int size = 0;
            while (true) {
                MemorySegment current = seg.get(libffidemo_h.C_POINTER, libffidemo_h.C_POINTER.byteSize() * size);
                if (current.address() == 0) {
                    break;
                }
                size += 1;
            }
            final Complex[] array = new Complex[size];
            for (int i = 0; i < size; i++) {
                MemorySegment current = seg.get(libffidemo_h.C_POINTER, libffidemo_h.C_POINTER.byteSize() * i);

                final Complex c = new Complex();
                c.setId(complex_t.id$get(current));
                final MemorySegment nameSeg = complex_t.name$get(current);
                c.setName(nameSeg.getUtf8String(0));
                c.setScore(complex_t.score$get(current));
                array[i] = c;
                free(nameSeg);
                free(current);
            }
            free(seg);
            return array;
        }
    }

    @SneakyThrows
    private static void free(MemorySegment resp) {
        free.invokeExact(resp);
    }

    public static void main(String[] args) {
        final PanamaDemo demo = new PanamaDemo();
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
