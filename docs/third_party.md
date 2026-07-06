# 第三方代码说明

## 当前实际使用的 vendor 源码

### `yare/src/main/jni/external/lsplant`

作为主要 Java ART Hook 引擎使用。

Yare 当前依赖 `LSPlant` 提供：

- Java 方法 Hook / Unhook
- backup method 生成
- 与 deoptimization 相关的 Hook 行为
- 未初始化静态方法 Hook 正确性

### `yare/src/main/jni/external/dobby`

作为 `LSPlant` 所需的 inline hook 后端使用。

Yare 在 `yare.cpp` 中通过 `InlineHooker` / `InlineUnhooker` 对 Dobby 做桥接。

### `yare/src/main/jni/external/xz-embedded`

当前 native 构建集成中保留的第三方依赖。

## Yare 自有 native 文件

以下 native 文件已经属于 Yare 当前项目的一部分，不应再视为 `upstream` 运行时依赖：

- `elf_image.*`
- `hidden_api.*`
- `profile_saver.*`
- `yare.cpp`

这些逻辑虽然在项目早期有过参考和移植过程，但现在已经是 Yare 自己维护的代码。

## 关于 `upstream/`

`upstream/` 只在项目早期用于参考：

- Pine core API 形状
- Pine sample 组织形式
- Pine 来源的一些 helper 实现

项目规则：

- `upstream/` 不得成为构建依赖
- `upstream/` 不应成为日常开发前提
- 当本地文档与代码已足够自洽后，可以直接删除

## 未来升级建议

如果后续升级 vendor 依赖：

- 应有意识地升级 `LSPlant` 与 `Dobby`
- 升级后重新跑 sample 回归验证
- 重点关注：
  - 未初始化静态方法 Hook 行为
  - proxy hook 行为
  - `unhookPhysical()` 语义
  - hidden API 初始化顺序
