# EN!GMA

智威校园一卡通，Android 端随身看。

EN!GMA 是「智威校园 EX」的非官方 Android 客户端，方便随时查看一卡通的余额、消费流水与刷脸记录。本软件是对智威校园小程序的学习模仿，仅供学习交流使用。

> 本项目与智威智能（广州智威智能科技有限公司）无官方关联，仅为个人学习目的开发。

---

## 特性

### 概览主页

- 一眼掌握**余额**、**今日消费**、**今日笔数**、**最近消费**与**累计笔数**
- 卡片式布局，数据异常时自动等待刷新

### 流水记录

- **消费流水** 与 **刷脸记录** 双标签页
- 明细含商户、地点、设备、时间、交易后余额、流水号、钱包
- 「查看全部」一键跳转记录页

### 余额变动通知

- 后台服务轮询一卡通消费，变动即时推送通知
- 可在设置中关闭同步以停止接收通知

### 初始化引导

- 三步配置：选择学校 → 选择年级班级 → 填写姓名与卡号
- 学校支持关键字搜索

### 自动更新

- 启动时检查 GitHub Release 新版本，弹窗引导更新

### 设置与维护

- 清除软件数据（登录信息、缓存）
- 仪表板条目天数限制、数据刷新频率
- 崩溃数据分析授权开关
- 软件细节（权限说明）

---

## 界面预览

（截图待补充）

---

## 快速开始

### 下载 Release

从 [Releases](https://github.com/ju1c3rSH/enigma/releases) 下载 APK，安装到 Android 设备（需 minSdk 24 / Android 7.0+）。

### 源码构建

> 需要 **JDK 17**。JDK 21 会因 kapt 与 `com.sun.tools.javac` 的兼容问题导致 `:app:kaptGenerateStubsDebugKotlin` 失败，请使用 JDK 17。

```bash
git clone https://github.com/ju1c3rSH/enigma.git
cd enigma

# Windows
.\gradlew.bat :app:assembleRelease
# Linux / macOS
./gradlew :app:assembleRelease
```

构建产物位于 `app/build/outputs/apk/release/`。

首次启动会进入初始化引导，按提示选择学校、年级班级并填写个人信息即可使用。

---

## 项目结构

```
app/src/main/java/homes/gensokyo/enigma/
├── MainActivity.kt                  # 入口，Compose + HorizontalPager 双页导航
├── MainApplication.kt               # 全局初始化（Retrofit/OkHttp/Gson/Repository）
├── bean/                            # 数据模型（余额/成绩/流水/学校/学生等）
├── interface/                       # ApiService、GithubApiService
├── logic/
│   ├── DataReposity.kt              # 实际使用的 UserRepository（网络请求）
│   ├── database/                    # Room 数据库（AppDatabase / Dao / 实体）
│   └── repository/                  # 备用 Room UserRepository
├── service/
│   └── ZhiWeiDataGetterService.kt   # 余额变动通知后台轮询
├── ui/
│   ├── about/                       # 关于（许可、贡献者）
│   ├── compose/component/           # Compose 组件与 EnigmaTheme
│   ├── oobe/                        # 初始化引导
│   ├── overview/                    # 概览主页
│   ├── records/                     # 流水记录
│   └── setting/                     # 设置
├── util/                            # 常量、加解密、Cookie、网络、偏好设置等
└── viewmodel/                       # UsrdataModel、SharedViewModel
```

---

## 技术栈

- **语言**：Kotlin 1.9
- **构建**：AGP 8.4 / Gradle 8.6，单模块 `:app`
- **UI**：Jetpack Compose（Material 3），底部导航 + `HorizontalPager` 手势切换
- **网络**：Retrofit + OkHttp（自定义 CookieJar，明文流量经 `network_security_config` 放行）
- **序列化**：Gson
- **本地存储**：Room（数据库）、`SharedPreferences`（设置）
- **图片**：Coil
- **发布**：Release 开启 R8 混淆与资源压缩

---

## 许可证与免责声明

本软件仅供个人学习交流使用，方便 Android 端查看校园一卡通的日常使用情况。

- 本项目完全免费，可能存在较多漏洞，维护者仅保证自己能正常使用，存在问题请通过项目页 Issues 反馈。
- 本项目适配的接口均来自智威智能（广州智威智能科技有限公司）旗下「智威校园」（ivxiaoyuan），本项目不对其控制的安全性负责。
- 本项目将个人资料、账户密钥等信息缓存在安装设备本地，**不含任何上传个人信息的接口**。
- 真诚希望智威官方能够推出「智威校园 For Android」，使广大用户能用上官方版本。
- 本项目仅供个人学习交流使用，切勿进行传播、任何形式的盈利；未涉及的问题请参见国家有关法律法规，以国家法律法规为准。

项目仓库：https://github.com/ju1c3rSH/enigma
