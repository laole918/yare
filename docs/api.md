# API 说明

## 包结构

主包：

- `com.laole918.yare`

回调包：

- `com.laole918.yare.callback`

## `Yare`

### `ensureInitialized()`

首次使用时初始化 native 状态。

当前会做：

- 加载 `libyare.so`
- 解析 `HookRecord.callback`
- 初始化 hidden API 处理
- 初始化 `LSPlant`

大多数公开操作都会隐式触发它，但也可以在应用启动早期主动调用。

### `isInitialized()`

返回 Yare 的 native 初始化是否已经成功完成。

### `hook(Member, MethodHook)`

Hook 一个 Java `Method` 或 `Constructor`。

行为：

- 拒绝 abstract method
- 拒绝 class initializer
- 为目标方法维护 callback 集合
- 同一个目标方法只建立一层 native hook

### `hook(Member, MethodHook, boolean canInitDeclaringClass)`

与上面相同，但保留 Pine 风格的 declaring-class 初始化控制参数。

当前说明：

- 这个参数保留在 Yare Java API 中
- Yare 没有为它再额外补一层 Pine-enhances 兼容实现
- 未初始化静态方法的 native 行为由 `LSPlant` 自身处理

### `isHooked(Member)`

检查目标成员当前是否存在 Yare hook record。

### `invokeOriginalMethod(Member, Object, Object...)`

调用原始实现。

行为：

- 如果目标已 hook，则通过保存的 backup method 调用
- 否则直接走反射调用原成员

### `decompile(Member, boolean disableJit)`

请求对方法做 deoptimization。

当前实现直接委托给 native `deoptimize0(...)`。

`disableJit` 参数目前主要用于保留 API 形状，当前 native 路径不会按它分支。

### `disableProfileSaver()`

关闭 ART profile saver。

当前 native 实现来源于 Pine 原实现移植。

### `disableHiddenApiPolicy(boolean application, boolean platform)`

关闭 hidden API 限制。

行为：

- 如果已经初始化，则立即生效
- 否则先记录到配置中，等初始化时再应用

## `MethodHook`

基础 callback 类型。

### `beforeCall(Yare.CallFrame)`

原始方法调用前执行。

允许：

- 修改 `args`
- 调用 `setResult(...)`
- 调用 `setThrowable(...)`

### `afterCall(Yare.CallFrame)`

原始方法调用后执行，或在 early return 后执行。

允许：

- 改写结果
- 改写异常

## `MethodHook.Unhook`

### `unhook()`

逻辑 unhook。

行为：

- 从 callback 集合中移除当前 Java callback
- 不一定会拆掉底层 native hook

### `unhookPhysical()`

物理 unhook。

行为：

- 移除当前 Java callback
- 如果目标已无 callback，则进一步调用 native `unhook0(...)`

只在你明确希望 teardown 底层 hook 时使用它。

## `MethodReplacement`

`MethodHook` 的便捷子类。

行为：

- replacement 逻辑在 `beforeCall` 中执行
- `afterCall` 被固定为空实现

辅助项：

- `DO_NOTHING`
- `returnConstant(Object result)`

## `Yare.CallFrame`

传入 callback 的可变调用上下文。

字段：

- `method`
- `thisObject`
- `args`

主要方法：

- `getResult()`
- `setResult(...)`
- `setResultIfNoException(...)`
- `getThrowable()`
- `setThrowable(...)`
- `hasThrowable()`
- `invokeOriginalMethod()`

## `YareConfig`

全局配置项。

重要字段：

- `sdkLevel`
- `debug`
- `debuggable`
- `disableHooks`
- `antiChecks`
- `disableHiddenApiPolicy`
- `disableHiddenApiPolicyForPlatformDomain`
- `libLoader`

典型用法：

- 在首次 `Yare.ensureInitialized()` 之前设置
- 如果宿主环境需要自定义加载逻辑，可以覆盖 `libLoader`

## 当前已知限制

- 没有单独暴露 Pine-enhances 风格兼容 API
- 没有 class-init monitor API
- 对自定义 `HookHandler` 的稳定性暂未提供正式兼容保证，当前主要按项目内部使用语义维护
