/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_nnk_rechargeplatform_utils_JniHelper */

#ifndef _Included_com_nnk_rechargeplatform_utils_JniHelper
#define _Included_com_nnk_rechargeplatform_utils_JniHelper
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_initKeys
        (JNIEnv *, jobject);

JNIEXPORT jbyteArray JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_BlowFishWithBase64
        (JNIEnv *, jobject, jint, jstring, jstring);

JNIEXPORT jstring JNICALL Java_com_nnk_rechargeplatform_utils_JniHelper_PiaGenDHZZ
        (JNIEnv *, jobject, jstring, jint, jstring);

jstring ctojstring(JNIEnv *env, char *tmpstr);

void correctUtfBytes(char *bytes);

#ifdef __cplusplus
}
#endif
#endif
