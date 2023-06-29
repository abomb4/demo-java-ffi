#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "org_example_JniDemo.h"
#include "libffidemo.h"

JNIEXPORT jint JNICALL Java_org_example_JniDemo_nativeSum (JNIEnv *env, jobject thisObject, jint a, jint b) {
    return demo_sum(a, b);
}

JNIEXPORT jstring JNICALL Java_org_example_JniDemo_nativeHello (JNIEnv *env, jobject thisObject, jstring name) {
    const char *name_chars = (*env)->GetStringUTFChars(env, name, JNI_FALSE);
    char *hello = demo_generate_hello(name_chars);
    (*env)->ReleaseStringUTFChars(env, name, name_chars);
    jstring utf = (*env)->NewStringUTF(env, hello);
    free(hello);
    return utf;
}

JNIEXPORT jobject JNICALL Java_org_example_JniDemo_nativeGetById(JNIEnv *env, jobject obj, jint id) {
    complex_t *c_entry = get_by_id(id);
    if (c_entry == NULL) {
        return NULL;
    }

    // Get the Complex class and its field IDs
    jclass complexClass = (*env)->FindClass(env, "org/example/Complex");
    jmethodID complexConstructor = (*env)->GetMethodID(env, complexClass, "<init>", "()V");
    jfieldID idField = (*env)->GetFieldID(env, complexClass, "id", "I");
    jfieldID nameField = (*env)->GetFieldID(env, complexClass, "name", "Ljava/lang/String;");
    jfieldID scoreField = (*env)->GetFieldID(env, complexClass, "score", "D");

    // Create a Java object of Complex class
    jstring jname = (*env)->NewStringUTF(env, c_entry->name);
    jobject complexObj = (*env)->NewObject(env, complexClass, complexConstructor);
    (*env)->SetIntField(env, complexObj, idField, c_entry->id);
    (*env)->SetObjectField(env, complexObj, nameField, jname);
    (*env)->SetDoubleField(env, complexObj, scoreField, c_entry->score);

    // // Release local references and free C memory
    (*env)->DeleteLocalRef(env, jname);
    free(c_entry->name);
    free(c_entry);

    return complexObj;
}

JNIEXPORT jobjectArray JNICALL Java_org_example_JniDemo_nativeGetByNameLike(JNIEnv *env, jobject obj, jstring name) {
    const char *search_name = (*env)->GetStringUTFChars(env, name, NULL);

    // Call get_by_name_like function
    complex_t **c_result = get_by_name_like(search_name);

    // Count the number of entries
    int count = 0;
    complex_t **temp = c_result;
    while (*temp != NULL) {
        count++;
        temp++;
    }

    // Get the Complex class and its field IDs
    jclass complexClass = (*env)->FindClass(env, "org/example/Complex");
    jmethodID complexConstructor = (*env)->GetMethodID(env, complexClass, "<init>", "()V");
    jfieldID idField = (*env)->GetFieldID(env, complexClass, "id", "I");
    jfieldID nameField = (*env)->GetFieldID(env, complexClass, "name", "Ljava/lang/String;");
    jfieldID scoreField = (*env)->GetFieldID(env, complexClass, "score", "D");

    // Create a Java array to store the result
    jobjectArray resultArray = (*env)->NewObjectArray(env, count, complexClass, NULL);

    // Copy each entry to a Java object and add it to the result array
    for (int i = 0; i < count; i++) {
        complex_t *entry = c_result[i];
        jint id = entry->id;
        jstring jname = (*env)->NewStringUTF(env, entry->name);
        jdouble score = entry->score;

        jobject complexObj = (*env)->NewObject(env, complexClass, complexConstructor);
        (*env)->SetIntField(env, complexObj, idField, id);
        (*env)->SetObjectField(env, complexObj, nameField, jname);
        (*env)->SetDoubleField(env, complexObj, scoreField, score);
        (*env)->SetObjectArrayElement(env, resultArray, i, complexObj);

        // Release local references and free C memory
        (*env)->DeleteLocalRef(env, jname);
        free(entry->name);
        free(entry);
        (*env)->DeleteLocalRef(env, complexObj);
    }

    // Release the C memory and name string
    free(c_result);
    (*env)->ReleaseStringUTFChars(env, name, search_name);

    return resultArray;
}
