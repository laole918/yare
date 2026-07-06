# 架构说明

## 目标

Yare 提供一个 Android Java Hook 库：

- Java 使用方式尽量贴近 Pine core
- 实际 Hook 引擎使用 `LSPlant`

## 当前分层

### Java 层

- `com.laole918.yare.Yare`
- `com.laole918.yare.YareConfig`
- `com.laole918.yare.callback.MethodHook`
- `com.laole918.yare.callback.MethodReplacement`

职责：

- 对外 API
- callback 列表管理
- hook 生命周期管理
- invoke-original 分发
- 区分逻辑 unhook 和物理 unhook

### Native 层

- `yare.cpp`
- `hidden_api.*`
- `profile_saver.*`
- `elf_image.*`

职责：

- 初始化 `LSPlant`
- 初始化并解析 `libart.so`
- 将 Java hook 请求桥接到 `LSPlant`
- 实现 hidden API 关闭
- 实现 profile saver 关闭
- 提供 deoptimize 入口

### Vendor native 依赖

- `LSPlant`
- `Dobby`
- `xz-embedded`

其中：

- `LSPlant` 是真实的 ART Hook 实现
- `Dobby` 作为 `LSPlant` 所需的 inline hook 后端

## API 映射

Yare 当前已经覆盖的 Pine core 风格能力：

- Hook Java 方法与构造函数
- callback 链式调用
- invoke original method
- method replacement
- method deoptimize
- disable hidden API policy
- disable profile saver

Yare 当前没有单独暴露 Pine enhances 风格的独立 API 层。

## Delay hook / enhances 立场

Pine 的 `enhances` 主要是为了解决：

- 未初始化类的静态方法 Hook
- 避免类初始化后 ART 入口点被改回去

在 Yare 下，这个问题的处理方式不同：

- Yare 底层使用的是 `LSPlant`，不是 Pine core
- `LSPlant` 内部已经处理了相关 ART 路径，包括类初始化后的静态 trampoline 恢复
- 因此 Yare 当前不重复实现：
  - `PineEnhances`
  - `PendingHookHandler`
  - `ClassInitMonitor`

当前项目策略：

- 保留 sample 中的行为测试
- 除非出现真实使用方需求，否则不额外补一层兼容 facade

## Unhook 语义

Yare 明确区分两种 unhook：

- `unhook()`
  - 只移除 Java callback
  - 保留底层 native hook
  - 适合作为默认语义，便于多 callback 场景稳定运行

- `unhookPhysical()`
  - 先移除 Java callback
  - 若该目标已无 callback，则继续调用 native `unhook0(...)`

这样设计的原因是：

- 立即物理 unhook 在 callback 复杂场景下更不稳定
- 每次逻辑 unhook 都直接 teardown 底层 hook 风险更大

## Hidden API 初始化顺序

当前 native 初始化顺序为：

1. 打开并解析 `libart.so`
2. 如有配置，先关闭 hidden API 限制
3. 再初始化 `LSPlant`

这样可以避免：

- `LSPlant` 初始化时先访问隐藏 ART 成员
- 但 hidden API exemption 还没有提前生效

## Sample 的角色

`sample` 不是单纯 demo，而是 Yare 当前的可执行能力矩阵。

当修改 Hook 行为时，应当：

1. 更新库实现
2. 补或改对应 sample 测试
3. 保证 sample 中的测试项和真实支持能力一致

不要保留“实际上已支持，但测试里却写成 unsupported”的假占位。
