#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/system_properties.h>
#include "native-lib.h"
#include "md5c.h"
#include <android/log.h>
#define LOG_TAG "NativeApi"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)


JNIEXPORT jstring JNICALL
Java_com_readboy_mathproblem_NativeApi_stringFromJNI(
        JNIEnv* env,
        jobject thiz) {
        LOGE("log in c");
    return (*env)->NewStringUTF(env, "Hello form C");
}

time_t getCurrentTime()
{
    time_t rawtime;
    struct tm * timeinfo;
    time(&rawtime);
    timeinfo = localtime(&rawtime);
   return rawtime;
}

JNIEXPORT jstring JNICALL
Java_com_readboy_mathproblem_NativeApi_getSignature(
        JNIEnv* env,
        jobject thiz,
        jstring date){

//通过C语言获取当前时间
//    time_t rawtime;
//    struct tm * timeinfo;
//    time(&rawtime);
//    timeinfo = localtime(&rawtime);
//    char dateStr[32];
//    sprintf(dateStr, "%d", rawtime);
    char* dateStr = (char*)(*env)->GetStringUTFChars(env, date, NULL);
//    return (*env)->NewStringUTF(env, (char*)dateStr);
//
    char model[32];
    const char* name = "ro.product.model";
    int len = __system_property_get(name, model);

    const char* separator = "/";
    const char* separators = "////////";
    const char* package = "com.readboy.mathproblem";

    char* device_id = malloc(strlen(model)+strlen(separator)+strlen(separators)+strlen(package)+3);
    strcpy(device_id, model);
    strcat(device_id, separator);
    strcat(device_id, separator);
    strcat(device_id, package);
    strcat(device_id, separators);

//    return (*env)->NewStringUTF(env, (char*)device_id);

    const char* app_secret = "358b4";

    unsigned char* source = malloc(strlen(device_id)+strlen(app_secret)+strlen(dateStr)+1);
    strcpy(source, device_id);
    strcat(source, app_secret);
    strcat(source, dateStr);

//    return (*env)->NewStringUTF(env, (char*)source);

    unsigned char* newIMEI = "0123456789abcdefoo";

    MD5_CTX md5;
    MD5Init(&md5);
    unsigned char decrypt[16];
    unsigned int inputLen = strlen(source);
    MD5Update(&md5, source, inputLen);
    MD5Final(&md5, decrypt);

    char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
                         'a','b','c','d','e','f'};
    char md5Str32[33];
    int h = 0;
    int k;
    for(k = 0; k < 16; k++){
        md5Str32[h++] = hexDigits[decrypt[k] / 16];
        md5Str32[h++] = hexDigits[decrypt[k] % 16];
    }

    md5Str32[32] = '\0';

    (*env)->ReleaseStringUTFChars(env, date, dateStr);

    return (*env)->NewStringUTF(env, (char*)md5Str32);
}
