# PulseFit / 脉冲健身

一个完全本地运行的 Android 健身助手应用，用于学习动作姿势和管理个人训练签到。

## 主要功能

- **健身签到**：记录每次训练的开始/结束时间、训练类型（肩部、背部、跑步、骑车等）和备注。
- **训练统计**：按周/月查看训练次数、总时长、类型分布，并支持 Excel 导出。
- **动作学习**：基于开源数据集 [exercises-dataset](https://github.com/hasaneyldrm/exercises-dataset) 整理，支持分类浏览、中英文名称、动作图片/GIF 演示和自定义训练便签。
- **设置**：用户 ID、免责声明、数据来源说明。

## 技术栈

- **Jetpack Compose** + Material 3
- **MVVM** + Hilt 依赖注入
- **Room** 本地数据库 + DataStore 偏好设置
- **Navigation Compose** 底部导航
- **Haze** 毛玻璃效果
- **Coil** 图片/GIF 加载
- **FastExcel** Excel 导出

## 开发方式

本项目使用 **Devin Cloud 模式** 进行 VibeCoding 尝试：由自然语言描述驱动，逐步迭代 UI、数据库、业务逻辑和资源集成，最终打包并交付可安装的 APK。

## 下载安装

- **Release**: [v1.0.0](https://github.com/Hygge1024/ProjectPulse/releases/tag/v1.0.0)
- **APK 下载**: [app-debug.apk](https://github.com/Hygge1024/ProjectPulse/releases/download/v1.0.0/app-debug.apk)

> 要求 Android 8.0（API 26）及以上。所有数据均存储在设备本地，应用不会进行任何联网操作。

## 构建

```bash
./gradlew app:assembleDebug --no-daemon
```

构建产物位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 数据来源与声明

动作数据、图片和 GIF 来自开源项目 [hasaneyldrm/exercises-dataset](https://github.com/hasaneyldrm/exercises-dataset)。
本项目仅供学习参考，完全开源、非商用。如发现侵权内容，请联系作者及时处理。

## 作者

- GitHub: [@Hygge1024](https://github.com/Hygge1024)
