# Yare

Yare 是一个 Android Java Hook 库，底层基于 `LSPlant`，对外 Java API 风格尽量向 Pine core 的使用方式靠拢。

## 模块

- `yare`：Android library 模块，包名 `com.laole918.yare`
- `sample`：Android app 模块，用于作为 `yare` 的能力验证与回归测试入口

## 当前设计

- Java 方法 Hook / Unhook：`LSPlant`
- Inline hook 后端：`Dobby`
- 额外 native 支持代码：
  - `hidden_api.*`
  - `profile_saver.*`
  - `elf_image.*`
- STL 模式：`c++_static`

`upstream/` 只在项目早期用于参考，不属于当前产品设计的一部分，不应参与构建逻辑；当仓库内文档足够自洽后，可以直接删除。

## 构建

构建库：

```bash
./gradlew :yare:assembleRelease
```

构建 sample：

```bash
./gradlew :sample:assembleDebug
```

环境要求和产物路径见 [docs/build.md](/Users/laole918/workspace/android/laole918/yare/docs/build.md)。

## 对外 API

主要入口：

- `Yare.ensureInitialized()`
- `Yare.hook(...)`
- `Yare.invokeOriginalMethod(...)`
- `Yare.decompile(...)`
- `Yare.disableHiddenApiPolicy(...)`
- `Yare.disableProfileSaver()`
- `MethodHook`
- `MethodReplacement`

API 语义和当前限制见 [docs/api.md](/Users/laole918/workspace/android/laole918/yare/docs/api.md)。

## 测试

`sample` 是当前的回归测试集合，覆盖：

- 普通 Java 方法 Hook
- 构造函数 Hook
- 代理类 Hook
- invoke original 流程
- JNI Hook 目标
- 未初始化静态方法的 delay-hook 等价行为
- 参数传递场景

详见 [docs/testing.md](/Users/laole918/workspace/android/laole918/yare/docs/testing.md)。

## 架构

详见 [docs/architecture.md](/Users/laole918/workspace/android/laole918/yare/docs/architecture.md)。

## 第三方代码

详见 [docs/third_party.md](/Users/laole918/workspace/android/laole918/yare/docs/third_party.md)。
