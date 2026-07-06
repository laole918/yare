# 测试说明

## `sample` 的角色

`sample` 是 Yare 当前的行为回归测试集合。

应将它视为：

- 能力验证集
- smoke test app
- 当前支持 Hook 流程的可执行文档

其中不应保留“实际没实现，只是列表里占位”的伪测试。

## 主要测试分组

### 核心 Hook 流程

- 非静态方法 Hook
- 静态方法 Hook
- direct/private 方法 Hook
- 构造函数 Hook
- 异常改写
- invoke-original 流程
- method replacement

### 与初始化时序相关的 Hook 流程

- `NotInitedTest`
- `DelayHookTest`

其中 `DelayHookTest` 的语义是：

- 验证 Yare 基于 `LSPlant` 时，未初始化静态方法可以正确 Hook
- 它不是 Pine-enhances 那种 enable/disable 开关测试

### 参数传递 / 调用约定覆盖

- `Arg0Test`
- `Arg4/8/...`
- `ArgLLLILLZZZIIILTest`

这些用例用于验证不同 primitive / object 参数布局下的传参与回调行为。

### 特殊目标

- proxy hook
- JNI dynamic lookup target
- JNI direct-register target

### 辅助运行时检查

- hidden API access
- toast hook
- GC test

## 构建验证

主要验证命令：

```bash
./gradlew :sample:assembleDebug
```

它是以下改动后的基础编译检查：

- 库 API 变更
- Hook 语义变更
- JNI / CMake 变更
- sample 测试新增或调整

## 维护规则

修改 Yare 行为时：

1. 更新库实现
2. 补或改 sample 测试
3. 让测试项名称与真实支持行为一致

如果某项能力真的不支持：

- 删除对应假测试
- 或在文档里明确标注不支持

不要保留“实际上可用，但 UI 里直接写 unsupport”的误导性测试项。
