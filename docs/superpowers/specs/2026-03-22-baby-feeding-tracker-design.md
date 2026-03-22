# 宝宝喂养时间记录 APP 设计文档

## 1. 项目概述

**项目名称**: BabyFeedTracker
**项目类型**: Android 原生移动应用
**核心功能**: 记录宝宝喂养时间、奶量、辅食，支持家庭局域网内多设备数据共享
**目标用户**: 有0-12个月宝宝的家庭，支持多名照护者协作记录

---

## 2. 技术栈

| 组件 | 技术选型 |
|------|---------|
| 平台 | Android (minSdk 26, targetSdk 34) |
| 语言 | Kotlin 1.9+ |
| UI框架 | Jetpack Compose + Material Design 3 |
| 架构 | MVVM + Clean Architecture |
| 本地数据库 | Room |
| 局域网通信 | HTTP Server (NanoHTTPD) + OkHttp |
| 依赖注入 | Hilt |
| 异步 | Kotlin Coroutines + Flow |
| 时间处理 | java.time (LocalDateTime) |

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

- 同一WiFi下自动发现设备
- 主设备开启热点或连接到同一路由器后成为服务器
- 数据实时同步到其他设备
- 支持最多5台设备同时连接

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

### 5.1 核心实体

```kotlin
@Entity(tableName = "feeding_records")
data class FeedingRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val babyId: String,
    val type: FeedingType, // BOTTLE, SOLID_FOOD
    val timestamp: Long, // 毫秒时间戳
    val amountMl: Int? = null, // 瓶喂用
    val milkType: MilkType? = null, // BOTTLE用
    val foodType: FoodType? = null, // SOLID_FOOD用
    val foodAmount: FoodAmount? = null, // SOLID_FOOD用
    val acceptance: Acceptance? = null, // SOLID_FOOD用
    val note: String? = null,
    val createdBy: String, // 设备ID
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "babies")
data class Baby(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val birthDate: Long, // 毫秒时间戳
    val avatarPath: String? = null
)

data class Device(
    val id: String,
    val name: String,
    val isServer: Boolean,
    val lastSyncTime: Long
)
```

### 5.2 枚举类型

```kotlin
enum class FeedingType { BOTTLE, SOLID_FOOD }
enum class MilkType { BREAST_MILK, FORMULA }
enum class FoodType { RICE_CEREAL, FRUIT_PUREE, VEGETABLE_PUREE, MEAT_PUREE, OTHER }
enum class FoodAmount { SMALL, MEDIUM, LARGE }
enum class Acceptance { LIKED, OKAY, REFUSED }
```

---

## 6. 局域网同步机制

### 6.1 架构

- **服务器模式**: 拥有最新数据的设备作为服务器
- **客户端模式**: 其他设备连接到服务器同步数据
- **发现机制**: UDP广播发现同一WiFi下的设备

### 6.2 同步协议

**端口**: 8765
**发现端口**: 8766 (UDP广播)

**HTTP API 端点**:

| 方法 | 路径 | 描述 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | `/health` | 心跳检查 | - | `{"status": "ok"}` |
| GET | `/records?since={timestamp}` | 获取自timestamp后的记录 | - | `[FeedingRecord, ...]` |
| POST | `/records` | 创建记录 | `FeedingRecord` | `{"id": "..."}` |
| PUT | `/records/{id}` | 更新记录 | `FeedingRecord` | `{"success": true}` |
| DELETE | `/records/{id}` | 软删除记录 | - | `{"success": true}` |
| GET | `/babies` | 获取所有宝宝 | - | `[Baby, ...]` |
| POST | `/babies` | 创建宝宝 | `Baby` | `{"id": "..."}` |
| PUT | `/babies/{id}` | 更新宝宝 | `Baby` | `{"success": true}` |
| GET | `/sync/status` | 获取同步状态 | - | `{"lastSync": timestamp, "deviceCount": n}` |

**发现协议**:
```
1. 设备A发送 UDP广播 "BFT_DISCOVER" 到 8766 端口
2. 设备B响应 "BFT_HERE:{deviceId}:{deviceName}:{serverPort}" 到发送者
3. 设备A选择连接到设备B的 serverPort
4. 握手：GET /sync/status 交换最新记录时间戳
5. 按时间戳增量同步记录（GET /records?since={lastSync}）
6. 客户端每30秒 GET /health 保持心跳
```

**JSON 格式**: 所有请求/响应使用 `Content-Type: application/json`

### 6.3 冲突处理

- 同一设备ID的记录以最新updatedAt为准
- 新记录优先保留
- 删除操作同步为"软删除"标记

---

## 7. 验收标准

### 7.1 功能验收

- [ ] 可以添加瓶喂记录（奶量、奶类型、时间）
- [ ] 可以添加辅食记录（食物类型、食量、接受度）
- [ ] 历史记录按日期分组显示
- [ ] 可以查看今日、本周的喂养统计
- [ ] 可以管理宝宝信息（姓名、出生日期）
- [ ] 同一WiFi下两台设备可以发现彼此
- [ ] 数据可以在两台设备间同步

### 7.2 体验验收

- [ ] 添加一条记录操作步骤 ≤ 3步
- [ ] 页面切换流畅，无明显卡顿
- [ ] 界面配色温馨，适合育儿场景
- [ ] 文字大小合适，阅读舒适

### 7.3 技术验收

- [ ] 应用可以在 Android 8.0+ 运行
- [ ] 数据存储在本地 Room 数据库
- [ ] 遵循 MVVM 架构
- [ ] 代码结构清晰，模块间低耦合
