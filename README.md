1. Build and install [NdkBinderService](NdkBinderService/) APK. It contains an Android Service, whose binder implementation is done in C++ JNI layer using NDK Binder APIs.

$ ./gradlew NdkBinderService:assembleDebug
$ adb install -f NdkBinderService/build/outputs/apk/debug/NdkBinderService-debug.apk

2. Build and install [JavaBinderClient](JavaBinderClient/) APK. It contains an Android Activity, who binds the Service from `NdkBinderService` and talks to Service using Java Binder APIs.
$ ./gradlew JavaBinderClient:assembleDebug
$ adb install -f JavaBinderClient/build/outputs/apk/debug/JavaBinderClient-debug.apk

