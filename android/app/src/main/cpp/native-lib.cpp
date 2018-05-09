#include <jni.h>
//#include <string>

extern "C"
JNIEXPORT jstring JNICALL

/// Reserved: may need to add C++ functions in the future
Java_com_khan_baron_voicerecrpg_MainActivity_jniDummy(
        JNIEnv* env,
        jobject /* this */) {
    //std::string hello = "Hello from C++";
    //return env->NewStringUTF(hello.c_str());
    return env->NewStringUTF("hello");
}
