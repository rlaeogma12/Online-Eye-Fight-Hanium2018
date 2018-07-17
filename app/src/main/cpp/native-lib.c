#include <jni.h>

JNIEXPORT void JNICALL
Java_qol_fashionchecker_MainActivity_checkFashion(JNIEnv *env, jobject instance,
                                                  jlong cascadeClassifier_face, jlong matAddrInput,
                                                  jlong matAddrResult1, jlong matAddrResult2,
                                                  jlong matAddrResult3, jlong matAddrResult4,
                                                  jlong matAddrResult5, jlong matAddrResult6,
                                                  jobject scoreObj) {

    // TODO

}

JNIEXPORT void JNICALL
Java_qol_fashionchecker_MainActivity_loadImage(JNIEnv *env, jobject instance,
                                               jstring imageFileName_, jlong img) {
    const char *imageFileName = (*env)->GetStringUTFChars(env, imageFileName_, 0);

    // TODO

    (*env)->ReleaseStringUTFChars(env, imageFileName_, imageFileName);
}