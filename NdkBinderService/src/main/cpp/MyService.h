#pragma once

#include <aidl/com/scania/BnMyService.h>

using ndk::ScopedAStatus;

namespace aidl {
namespace com {
namespace scania {

class MyService : public BnMyService
{
public:
    ScopedAStatus setEnable(const std::string& packageName, bool isEnable) override;

    ScopedAStatus removePermission(const std::string& packageName, const std::string& permission) override;

    ScopedAStatus setConfig(const std::string& packageName,const std::string& config) override;

};

} // namespace scania
} // namespace com
} // namespace aidl
