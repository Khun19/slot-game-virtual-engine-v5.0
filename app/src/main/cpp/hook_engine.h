#ifndef HOOK_ENGINE_H
#define HOOK_ENGINE_H

#include <string>
#include <vector>
#include <mutex>
#include <android/log.h>

#define LOG_TAG "SlotSandboxNDK"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

struct HookEntry {
    std::string targetSymbol;
    std::string moduleName;
    bool active;
    uint32_t callCount;
};

struct DeviceSpoofProfile {
    std::string imei;
    std::string macAddress;
    std::string androidId;
    std::string deviceModel;
    std::string buildFingerprint;
    bool isRootHidden;
};

struct RtpPacketPayload {
    std::string serverHost;
    std::string protocolType; // SSL_read, WS_recv, socket_recv
    double calculatedRtp;
    std::string rawJsonPayload;
    uint64_t packetSequence;
};

struct FridaScript {
    std::string id;
    std::string name;
    std::string description;
    std::string category;
    std::string jsCode;
    bool active;
    int hookCount;
};

class HookEngine {
public:
    static HookEngine& getInstance();

    void initialize();
    bool hookSymbol(const std::string& module, const std::string& symbol);
    void updateSpoofProfile(const DeviceSpoofProfile& profile);
    DeviceSpoofProfile getSpoofProfile() const;

    void setFpsUnlock(int targetFps, bool directGpuPassThrough);
    int getTargetFps() const { return targetFps_; }
    bool isGpuPassThroughEnabled() const { return gpuPassThroughEnabled_; }

    std::string interceptAndParseNetworkRtp(const std::string& serverHost, bool isSslActive);

    // Frida Hooking Engine Methods
    bool initFridaGadget();
    std::string injectFridaScript(const std::string& scriptId, const std::string& name, const std::string& jsCode);
    bool detachFridaScript(const std::string& scriptId);
    std::string setGameSpeedMultiplier(float multiplier);
    std::vector<FridaScript> getFridaScripts();

    void addLog(const std::string& category, const std::string& message);
    std::vector<std::string> getLogs();

    const std::vector<HookEntry>& getActiveHooks() const { return hooks_; }

private:
    HookEngine();
    ~HookEngine() = default;

    mutable std::mutex engineMutex_;
    DeviceSpoofProfile spoofProfile_{
        "867543029108234",
        "02:00:00:4A:8B:11",
        "9774d56d682e549c",
        "Samsung Galaxy S24 Ultra (Sandbox Virtual)",
        "google/raven/raven:14/UP1A.231105.003/11018593:user/release-keys",
        true
    };
    std::vector<HookEntry> hooks_;
    std::vector<FridaScript> fridaScripts_;
    std::vector<std::string> logBuffer_;
    int targetFps_ = 120;
    bool gpuPassThroughEnabled_ = true;
    bool fridaGadgetAttached_ = false;
    float speedMultiplier_ = 1.0f;
};

#endif // HOOK_ENGINE_H
