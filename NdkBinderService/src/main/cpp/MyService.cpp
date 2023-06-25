#include "MyService.h"
#include <LogDefs.h>

using aidl::com::scania::MyService;

ScopedAStatus MyService::setEnable(const std::string& packageName,bool isEnable){
    LOGD("[MyService] [cpp] setEnable: packageName=%s, isEnable=%d ", packageName.c_str(), isEnable);
    return ScopedAStatus::ok();
}

ScopedAStatus MyService::removePermission(const std::string& packageName, const std::string& permission){
    LOGD("[MyService] [cpp] removePermission: packageName=%s, permission=%s ", packageName.c_str(), permission.c_str());
    return ScopedAStatus::ok();
}

ScopedAStatus MyService::setConfig(const std::string& packageName,const std::string& config) {
    LOGD("[MyService] [cpp] setConfig: packageName=%s, config=%s ", packageName.c_str(), config.c_str());
    return ScopedAStatus::ok();
}