/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_example_JniDemo */

#ifndef _Included_org_example_JniDemo
#define _Included_org_example_JniDemo
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_example_JniDemo
 * Method:    nativeSum
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_example_JniDemo_nativeSum
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_example_JniDemo
 * Method:    nativeHello
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_example_JniDemo_nativeHello
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_example_JniDemo
 * Method:    nativeGetById
 * Signature: (I)Lorg/example/Complex;
 */
JNIEXPORT jobject JNICALL Java_org_example_JniDemo_nativeGetById
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_example_JniDemo
 * Method:    nativeGetByNameLike
 * Signature: (Ljava/lang/String;)[Lorg/example/Complex;
 */
JNIEXPORT jobjectArray JNICALL Java_org_example_JniDemo_nativeGetByNameLike
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif