// IMyService.aidl
package com.scania;

// Declare any non-default types here with import statements
interface IMyService {

     void setEnable(String packageName, boolean isEnable);

     void removePermission(String packageName, String permission);

     void setConfig(String packageName,String config);
}
