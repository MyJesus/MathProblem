#include <jni.h>
#ifndef _NATIVE_LIB_H_
#define _NATIVE_LIB_H_
#ifdef __cplusplus
extern "C"{
#endif


JNIEXPORT jstring JNICALL
Java_com_readboy_mathproblem_NativeApi_stringFromJNI(
        JNIEnv *,
        jobject);

JNIEXPORT jstring JNICALL
Java_com_readboy_mathproblem_NativeApi_getSignature(
        JNIEnv* env,
        jobject,
        jstring);

#ifdef __cplusplus
}
#endif
#endif