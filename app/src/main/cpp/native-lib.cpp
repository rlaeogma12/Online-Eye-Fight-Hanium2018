#include <jni.h>
#include <string>

extern "C"
JNIEXPORT void JNICALL
Java_qol_fashionchecker_ResultActivity_loadImage(JNIEnv *env, jobject instance,
                                                 jstring imageFileName_, jlong addrImage) {

}

extern "C"
JNIEXPORT jlong JNICALL
Java_qol_fashionchecker_MainActivity_loadCascade(JNIEnv *env, jobject instance,
                                                 jstring cascadeFileName_) {
    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);

    // TODO

    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}