#include <jni.h>
#include <string>
#include <vector>
#include "hook_engine.h"

extern "C" JNIEXPORT void JNICALL
Java_com_example_sandbox_SandboxNativeBridge_initSandboxEngine(
        JNIEnv* env,
        jobject /* this */) {
    HookEngine::getInstance().initialize();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_sandbox_SandboxNativeBridge_loadApkContainer(
        JNIEnv* env,
        jobject /* this */,
        jstring jApkPath,
        jstring jPackageName) {
    const char* apkPath = env->GetStringUTFChars(jApkPath, nullptr);
    const char* packageName = env->GetStringUTFChars(jPackageName, nullptr);

    std::string pathStr(apkPath);
    std::string pkgStr(packageName);

    HookEngine::getInstance().addLog("LXC_SANDBOX", "Isolated user-space container instantiated for APK: " + pkgStr);
    HookEngine::getInstance().addLog("APK_STAGING", "Parsed APK manifest & assets from path: " + pathStr);
    HookEngine::getInstance().hookSymbol("libart.so", "LoadNativeLibrary");
    HookEngine::getInstance().hookSymbol("libc.so", "__system_property_get");

    env->ReleaseStringUTFChars(jApkPath, apkPath);
    env->ReleaseStringUTFChars(jPackageName, packageName);

    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_sandbox_SandboxNativeBridge_interceptSysCall(
        JNIEnv* env,
        jobject /* this */,
        jstring jModule,
        jstring jSyscall) {
    const char* moduleName = env->GetStringUTFChars(jModule, nullptr);
    const char* syscallName = env->GetStringUTFChars(jSyscall, nullptr);

    HookEngine::getInstance().hookSymbol(moduleName, syscallName);

    env->ReleaseStringUTFChars(jModule, moduleName);
    env->ReleaseStringUTFChars(jSyscall, syscallName);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_sandbox_SandboxNativeBridge_spoofDeviceIdentifier(
        JNIEnv* env,
        jobject /* this */,
        jstring jImei,
        jstring jMac,
        jstring jAndroidId,
        jstring jModel,
        jboolean jHideRoot) {
    const char* imei = env->GetStringUTFChars(jImei, nullptr);
    const char* mac = env->GetStringUTFChars(jMac, nullptr);
    const char* androidId = env->GetStringUTFChars(jAndroidId, nullptr);
    const char* model = env->GetStringUTFChars(jModel, nullptr);

    DeviceSpoofProfile profile;
    profile.imei = imei;
    profile.macAddress = mac;
    profile.androidId = androidId;
    profile.deviceModel = model;
    profile.isRootHidden = jHideRoot;

    HookEngine::getInstance().updateSpoofProfile(profile);

    env->ReleaseStringUTFChars(jImei, imei);
    env->ReleaseStringUTFChars(jMac, mac);
    env->ReleaseStringUTFChars(jAndroidId, androidId);
    env->ReleaseStringUTFChars(jModel, model);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_sandbox_SandboxNativeBridge_setGpuPassThrough(
        JNIEnv* env,
        jobject /* this */,
        jint targetFps,
        jboolean enablePassThrough) {
    HookEngine::getInstance().setFpsUnlock(targetFps, enablePassThrough);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_sandbox_SandboxNativeBridge_getEngineStatusLogs(
        JNIEnv* env,
        jobject /* this */) {
    std::vector<std::string> logs = HookEngine::getInstance().getLogs();

    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray((jsize)logs.size(), stringClass, nullptr);

    for (size_t i = 0; i < logs.size(); i++) {
        jstring logStr = env->NewStringUTF(logs[i].c_str());
        env->SetObjectArrayElement(result, (jsize)i, logStr);
        env->DeleteLocalRef(logStr);
    }

    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_sandbox_SandboxNativeBridge_interceptRtpPackets(
        JNIEnv* env,
        jobject /* this */,
        jstring jServerHost,
        jboolean jIsSslInterceptionActive) {
    const char* serverHost = env->GetStringUTFChars(jServerHost, nullptr);
    std::string hostStr(serverHost);

    std::string payload = HookEngine::getInstance().interceptAndParseNetworkRtp(hostStr, jIsSslInterceptionActive);

    env->ReleaseStringUTFChars(jServerHost, serverHost);
    return env->NewStringUTF(payload.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_sandbox_SandboxNativeBridge_injectFridaScript(
        JNIEnv* env,
        jobject /* this */,
        jstring jScriptId,
        jstring jScriptName,
        jstring jJsCode) {
    const char* scriptId = env->GetStringUTFChars(jScriptId, nullptr);
    const char* scriptName = env->GetStringUTFChars(jScriptName, nullptr);
    const char* jsCode = env->GetStringUTFChars(jJsCode, nullptr);

    std::string res = HookEngine::getInstance().injectFridaScript(scriptId, scriptName, jsCode);

    env->ReleaseStringUTFChars(jScriptId, scriptId);
    env->ReleaseStringUTFChars(jScriptName, scriptName);
    env->ReleaseStringUTFChars(jJsCode, jsCode);

    return env->NewStringUTF(res.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_sandbox_SandboxNativeBridge_detachFridaScript(
        JNIEnv* env,
        jobject /* this */,
        jstring jScriptId) {
    const char* scriptId = env->GetStringUTFChars(jScriptId, nullptr);

    bool ok = HookEngine::getInstance().detachFridaScript(scriptId);

    env->ReleaseStringUTFChars(jScriptId, scriptId);
    return ok ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_sandbox_SandboxNativeBridge_setGameSpeedMultiplier(
        JNIEnv* env,
        jobject /* this */,
        jfloat jMultiplier) {
    std::string res = HookEngine::getInstance().setGameSpeedMultiplier(jMultiplier);
    return env->NewStringUTF(res.c_str());
}
