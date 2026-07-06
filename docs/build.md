# 构建说明

## 工具链

当前项目配置：

- AGP：`8.9.3`
- compileSdk：`35`
- targetSdk：`35`
- minSdk：`21`
- NDK：`29.0.14206865`
- CMake：`3.28.0+`
- Java source/target：`11`

配置来源：

- [build.gradle.kts](/Users/laole918/workspace/android/laole918/yare/build.gradle.kts)
- [gradle/libs.versions.toml](/Users/laole918/workspace/android/laole918/yare/gradle/libs.versions.toml)

## Native 构建模式

`yare` 当前使用：

- `externalNativeBuild.cmake`
- `-DANDROID_STL=c++_static`
- `C++20`

使用 `c++_static` 的原因：

- 让库产物的运行时链接更简单
- 避免依赖额外分发 `libc++_shared.so`

## 常用命令

构建库 AAR：

```bash
./gradlew :yare:assembleRelease
```

构建 sample APK：

```bash
./gradlew :sample:assembleDebug
```

## 产物位置

常见产物：

- `yare/build/outputs/aar/yare-release.aar`
- `sample/build/outputs/apk/debug/sample-debug.apk`

## 常见警告

部分本地 Android SDK / NDK 环境可能出现以下警告：

- SDK XML version mismatch
- NDK 路径重复或不一致
- Android toolchain 引发的 CMake deprecation warning

这些警告不一定意味着构建失败，但应保留可见性，不要默认吞掉。

## 仓库规则

- `upstream/` 仅作参考，不得参与构建逻辑
- 构建产物不得提交进 git
- `sample/.gitignore` 与顶层 ignore 规则应持续排除 `build/`、`.cxx/` 等生成物
