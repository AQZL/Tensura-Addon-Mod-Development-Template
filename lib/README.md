# 本地开发依赖

本目录的六个完整 JAR 作为 `compileOnly` 和开发环境的 `localRuntime` 依赖；不会被打入附属的发布 JAR。它们读取自 `Tensura_extras_1.21.1/lib/`，原文件保持不变。

| 完整运行库 | 版本 |
| --- | --- |
| Tensura NeoForge | 2.0.0.1 |
| ManasCore NeoForge 聚合包 | 4.0.0.0 |
| Architectury | 13.0.8 |
| GeckoLib | 4.7.6 |
| SmartBrainLib | 1.16.11 |
| TerraBlender | 4.1.0.8 |

SmartBrainLib 的源文件名没有版本号；本目录按其实际模组元数据命名。JAR 内容没有修改。

`compile/` 的十个 ManasCore 模块全部从本目录的 `manascore-neoforge-4.0.0.0.jar` 中提取，版本统一为 4.0.0.0。Java 编译器不能直接读取聚合包内部的嵌套 JAR，因此需要这些独立编译依赖。开发运行时只加载聚合包，由 NeoForge 加载其内嵌模块。

**不要把 `compile/` 中的 JAR 额外安装到游戏 `mods/`。** 不使用旧版 `manascore-storage-neoforge-3.0.3.1.jar`，也不需要参考项目的 EMI、JEI、FTB 或其他专用兼容库。

更新依赖时，先替换完整 JAR，再从新 ManasCore 聚合包的 `META-INF/jars/` 中提取同版十个模块到 `compile/`，移除被替换的旧版本，最后同步修改 `gradle.properties` 和依赖清单。`verifyLocalLibraries` 会在编译前检查预期文件是否齐全。

`dependencies.json` 记录了每个文件的来源、用途及 SHA-256。所有第三方 JAR 保留其原始许可和元数据。
