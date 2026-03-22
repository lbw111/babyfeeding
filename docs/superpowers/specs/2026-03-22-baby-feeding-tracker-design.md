# 宝宝喂养时间记录 APP 设计文档

## 1. 项目概述

**项目名称**: BabyFeedTracker
**项目类型**: Android 原生移动应用
**核心功能**: 记录宝宝喂养时间、奶量、辅食，支持家庭局域网内多设备数据共享
**目标用户**: 有0-12个月宝宝的家庭，支持多名照护者协作记录

---

## 2. 技术栈

### Android 端

| 组件 | 技术选型 |
|------|---------|
| 平台 | Android (minSdk 26, targetSdk 34) |
| 语言 | Kotlin 1.9+ |
| UI框架 | Jetpack Compose + Material Design 3 |
| 架构 | MVVM + Clean Architecture |
| 网络 | Retrofit2 + OkHttp |
| 依赖注入 | Hilt |
| 异步 | Kotlin Coroutines + Flow |
| 本地缓存 | DataStore Preferences |

### 后端服务

| 组件 | 技术选型 |
|------|---------|
| 语言 | Java 17+ |
| 框架 | Spring Boot 3.x |
| 数据库 | MySQL 8.0 |
| ORM | Spring Data JPA |
| 发现服务 | Spring Boot 内嵌 + UDP |
| 端口 | 8765 (HTTP API), 8766 (UDP发现) |

---

## 3. 功能模块

### 3.1 喂养记录

**瓶喂记录**
- 喂养时间（开始时间，自动记录）
- 奶量（ml），支持滑动输入
- 奶类型：母乳 / 奶粉
- 备注（可选）

**辅食记录**
- 喂养时间
- 食物类型：米粉 / 果泥 / 菜泥 / 肉泥 / 其他
- 食量描述：少量 / 中等 / 大量
- 接受度：喜欢吃 / 一般 / 拒绝
- 备注（可选）

### 3.2 历史记录

- 按日期分组展示喂养历史
- 支持查看任意一天的记录
- 每条记录显示：时间、类型、关键信息
- 点击可查看详情或编辑

### 3.3 统计数据

- 今日喂养次数和总奶量
- 近7天喂养趋势（简单图表）
- 单次平均奶量

### 3.4 家庭共享

- 后端服务部署在局域网内某台机器上
- 同一WiFi下Android设备自动发现后端服务
- 所有数据通过后端服务统一存储到MySQL
- 多台Android设备可同时连接后端，数据实时共享

### 3.5 宝宝信息

- 宝宝姓名
- 出生日期（用于计算月龄）
- 头像（可选本地图片）

---

## 4. UI设计

### 4.1 配色方案

| 用途 | 颜色 | 色值 |
|------|------|------|
| 主色 | 珊瑚粉 | #FF8A80 |
| 次要色 | 奶油白 | #FFF8E1 |
| 强调色 | 薄荷绿 | #80CBC4 |
| 背景 | 暖白 | #FFFBFA |
| 文字主色 | 深棕 | #4E342E |
| 文字次色 | 中棕 | #8D6E63 |

### 4.2 页面结构

```
首页 (HomeScreen)
├── 今日概览卡片
├── 快速添加按钮 (+)
│   ├── 瓶喂记录
│   └── 辅食记录
├── 今日喂养列表
└── 底部导航栏

历史 (HistoryScreen)
├── 日期选择器
└── 喂养记录列表

统计 (StatsScreen)
├── 今日/本周切换
├── 喂养数据图表
└── 详细统计

设置 (SettingsScreen)
├── 宝宝信息管理
├── 设备连接管理
└── 关于
```

### 4.3 组件库

- FeedRecordCard: 喂养记录卡片
- BottleFeedForm: 瓶喂表单
- SolidFoodForm: 辅食表单
- DateSelector: 日期选择器
- StatCard: 统计卡片
- ConnectionStatus: 设备连接状态指示器

---

## 5. 数据模型

### 5.1 后端实体 (Spring Boot / JPA)

```java
@Entity
@Table(name = "feeding_records")
public class FeedingRecord {
    @Id
    private String id;
    private String babyId;
    @Enumerated(EnumType.STRING)
    private FeedingType type;
    private Long timestamp;
    private Integer amountMl;
    @Enumerated(EnumType.STRING)
    private MilkType milkType;
    @Enumerated(EnumType.STRING)
    private FoodType foodType;
    @Enumerated(EnumType.STRING)
    private FoodAmount foodAmount;
    @Enumerated(EnumType.STRING)
    private Acceptance acceptance;
    private String note;
    private String createdBy;
    private Long createdAt;
    private Long updatedAt;
    private Boolean deleted = false;
}

@Entity
@Table(name = "babies")
public class Baby {
    @Id
    private String id;
    private String name;
    private Long birthDate;
    private String avatarPath;
}
```

### 5.2 Android DTO

```kotlin
data class FeedingRecordDto(
    val id: String,
    val babyId: String,
    val type: FeedingType,
    val timestamp: Long,
    val amountMl: Int?,
    val milkType: MilkType?,
    val foodType: FoodType?,
    val foodAmount: FoodAmount?,
    val acceptance: Acceptance?,
    val note: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class BabyDto(
    val id: String,
    val name: String,
    val birthDate: Long,
    val avatarPath: String?
)
```

### 5.3 枚举类型

```kotlin
enum class FeedingType { BOTTLE, SOLID_FOOD }
enum class MilkType { BREAST_MILK, FORMULA }
enum class FoodType { RICE_CEREAL, FRUIT_PUREE, VEGETABLE_PUREE, MEAT_PUREE, OTHER }
enum class FoodAmount { SMALL, MEDIUM, LARGE }
enum class Acceptance { LIKED, OKAY, REFUSED }
```

```java
public enum FeedingType { BOTTLE, SOLID_FOOD }
public enum MilkType { BREAST_MILK, FORMULA }
public enum FoodType { RICE_CEREAL, FRUIT_PUREE, VEGETABLE_PUREE, MEAT_PUREE, OTHER }
public enum FoodAmount { SMALL, MEDIUM, LARGE }
public enum Acceptance { LIKED, OKAY, REFUSED }
```

---

## 6. 后端服务与同步机制

### 6.1 系统架构

```
┌─────────────┐      HTTP/REST       ┌──────────────────┐
│  Android    │◄────────────────────►│  Spring Boot     │
│  Device 1    │                      │  Backend         │
└─────────────┘                      │  (MySQL)         │
                                      └────────┬─────────┘
┌─────────────┐      HTTP/REST               │
│  Android    │◄──────────────────────────────┘
│  Device 2   │
└─────────────┘
```

- 后端服务部署在局域网内一台机器（电脑/NAS/树莓派）
- 所有Android设备通过HTTP REST API与后端通信
- 后端将数据存储在MySQL数据库
- Android设备通过UDP广播发现后端服务

### 6.2 发现机制

**端口**: 8765 (HTTP API), 8766 (UDP发现)

```
1. Android发送 UDP广播 "BFT_DISCOVER" 到 8766 端口
2. 后端服务响应 "BFT_HERE:{host}:{port}" 到发送者
3. Android获取后端地址，开始REST API调用
4. 每30秒发送心跳检查后端可用性
```

### 6.3 REST API 端点

| 方法 | 路径 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | `/health` | 心跳检查 | - | `{"status": "ok"}` |
| GET | `/api/records?since={timestamp}` | 获取自timestamp后的记录 | - | `[FeedingRecord, ...]` |
| POST | `/api/records` | 创建记录 | `FeedingRecord` | `{"id": "..."}` |
| PUT | `/api/records/{id}` | 更新记录 | `FeedingRecord` | `{"success": true}` |
| DELETE | `/api/records/{id}` | 软删除记录 | - | `{"success": true}` |
| GET | `/api/babies` | 获取所有宝宝 | - | `[Baby, ...]` |
| POST | `/api/babies` | 创建宝宝 | `Baby` | `{"id": "..."}` |
| PUT | `/api/babies/{id}` | 更新宝宝 | `Baby` | `{"success": true}` |

**JSON 格式**: 所有请求/响应使用 `Content-Type: application/json`

### 6.4 冲突处理

- 同一ID的记录以最新updatedAt为准更新
- 删除操作使用软删除（deleted=true）

---

## 7. 验收标准

### 7.1 功能验收

- [ ] 可以添加瓶喂记录（奶量、奶类型、时间）
- [ ] 可以添加辅食记录（食物类型、食量、接受度）
- [ ] 历史记录按日期分组显示
- [ ] 可以查看今日、本周的喂养统计
- [ ] 可以管理宝宝信息（姓名、出生日期）
- [ ] 后端服务启动后可以被Android设备发现
- [ ] 多台Android设备可以同时访问后端，数据同步

### 7.2 体验验收

- [ ] 添加一条记录操作步骤 ≤ 3步
- [ ] 页面切换流畅，无明显卡顿
- [ ] 界面配色温馨，适合育儿场景
- [ ] 文字大小合适，阅读舒适

### 7.3 技术验收

- [ ] Android应用可以在 Android 8.0+ 运行
- [ ] 后端服务可以在 Java 17+ 环境运行
- [ ] 数据存储在 MySQL 数据库
- [ ] Android端遵循 MVVM 架构
- [ ] 后端遵循 Spring Boot 三层架构（Controller/Service/Repository）
