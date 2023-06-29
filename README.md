# demo-java-ffi

Demo for java ffi, attach for article of 恒生技术.

To run this demo, you need JDK 20 installed.

This demo contains JNI, JNA, JNI, Panama example projects, those java project are using a C library in libffidemo. There is 4 methods in libffidemo includes simple sum method, simple string method, simple struct method and an array of structs returning method.

I've tested performance of each FFI mechanism on my i5-8225U computer, the result is:

| FFI    | TPS    |
| ------ | ------ |
| JNI    | 180000 |
| JNA    | 66000  |
| JNR    | 270000 |
| Panama | 420000 |

Panama is awesome, but it hasn't released, sad.
