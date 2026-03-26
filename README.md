## 一、项目定位

KOB 是一个面向编程爱好者的在线实时对战平台，支持玩家手动操作与 Bot 程序对战。  
我主要负责后端核心链路：**鉴权、匹配、对局启动、Bot 调用、对战同步与记录**。

---

## 二、核心功能

- 账号体系：注册、登录、JWT 鉴权、第三方登录
- 对战模式：人人对战 / 人机对战 / 机器对战
- 匹配系统：按分数 + 等待时长动态扩圈
- 实时通信：WebSocket 推送对局状态
- Bot 托管：提交代码并在沙箱服务执行
- 数据能力：排行榜、历史对局、回放

---

## 三、技术栈

- **后端**：Spring Boot + Spring Security + MyBatis-Plus
- **微服务**：Spring Cloud（Eureka + Gateway + OpenFeign）
- **通信**：HTTP（Feign）+ WebSocket
- **存储**：MySQL, Redis
- **部署**：Nginx + Linux + Docker

---

## 四、系统架构（面试重点）

### 1）入口层
前端统一请求 `Gateway`，避免前端直连多个后端服务，便于统一鉴权和路由管理。

### 2）注册发现
`backend`、`matchingsystem`、`botrunningsystem` 启动后注册到 `Eureka`，服务间通过服务名调用，降低硬编码耦合。

### 3）核心业务链路（玩家点击开始匹配）
1. 前端通过 WebSocket/HTTP 通知 `backend`
2. `backend` 调用 `matchingsystem` 添加玩家
3. `matchingsystem` 进入匹配池并周期匹配
4. 匹配成功后回调 `backend` 的开局接口
5. `backend` 创建 `Game`，并向双方 WebSocket 推送开局状态
6. 每一帧根据玩家输入 / Bot 决策更新状态并广播

### 4）Bot 链路
`backend` 在需要 Bot 决策时调用 `botrunningsystem`，获取方向后驱动游戏推进。

---

## 五、匹配算法设计

在 `matchingsystem` 的 `MatchingPool` 中：

- 维护玩家池（等待玩家列表）
- 每秒执行一次匹配循环
- 玩家等待时间每秒 +1
- 匹配条件：`|ratingA - ratingB| <= min(waitA, waitB) * 10`
- 匹配成功后从池中移除并回调开局接口

**设计价值：**
- 等待时间越长，匹配范围越大，平衡“公平性”和“等待时长”
- 逻辑简单、稳定、易调参，适合中小规模流量场景

---