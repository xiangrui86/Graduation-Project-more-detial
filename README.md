# 电商系统（前后端分离）

## 技术栈

| 层级     | 技术                    |
|----------|-------------------------|
| 后端语言 | Java 17                 |
| 后端框架 | Spring Boot 3.4.1       |
| 数据库   | MySQL                   |
| 前端语言 | Vue 2.6.14              |
| 前端UI   | Element UI              |
| 架构     | 前后端分离（REST API）  |

## 系统角色

- **管理员**：数据报表、个人中心、商品管理、交易管理、系统管理、开发配置、库存管理、资讯管理
- **商家**：数据报表、个人中心、商品管理、交易管理、客服消息、库存管理
- **用户**：商城首页、全部商品、购物车、我的收藏、我的订单、新品上架、猜您想买、分类精选、销量排行、客服聊天、商品评价、系统资讯、最新公告

## 支付方式：模拟支付

- 使用「模拟支付」接口，用户点击支付后直接标记订单为已支付。
- 前端仅提供支付确认按钮，无需跳转第三方支付页面。
- 保留支付记录核心字段：支付方式、支付时间、支付金额，保证数据完整性。

## 协同过滤推荐（猜您想买）

- **算法说明**：张三购买并收货了 A、B、C 三种商品，李四购买并收货了 A 商品，则系统会推荐李四购买 B、C 两种商品。
- 实现位置：后端 `CollaborativeFilteringService`，接口 `GET /api/pub/products/recommend?userId=xxx`。

---

## 快速开始

### 1. 数据库

创建 MySQL 数据库（如 8.x）：

```sql
CREATE DATABASE mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改后端配置 `backend/src/main/resources/application.yml` 中的数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 你的密码
```

### 2. 后端

```bash
cd backend
mvnw.cmd clean install   # Windows
# 或
./mvnw clean install      # Linux/Mac

mvnw.cmd spring-boot:run  # 启动，默认端口 8080，context-path /api
```

首次启动会自动建表并初始化数据（管理员、商家、用户、分类、示例商品、公告）。

### 3. 前端

```bash
cd frontend
npm install
npm run serve
```

访问：http://localhost:8081  
前端通过 vue.config.js 将 `/api` 代理到后端 `http://localhost:8080`。

### 4. 默认账号（初始化后）

| 角色   | 用户名   | 密码       |
|--------|----------|------------|
| 管理员 | admin    | admin123   |
| 商家   | merchant | merchant123|
| 用户   | user     | user123    |

---

## 项目结构

```
code/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/com/mall/
│   │   ├── config/          # 配置（Security、JWT、数据初始化）
│   │   ├── controller/      # 控制器（auth、pub、user、merchant、admin）
│   │   ├── dto/
│   │   ├── entity/          # JPA 实体
│   │   ├── repository/      # 数据访问
│   │   ├── security/        # JWT 过滤与工具
│   │   └── service/         # 业务（协同过滤、模拟支付等）
│   └── src/main/resources/
│       └── application.yml
├── frontend/                # Vue 2 前端
│   ├── src/
│   │   ├── api/             # 请求封装（auth、pub、user、admin、merchant）
│   │   ├── components/
│   │   ├── layouts/         # 用户/管理员/商家布局
│   │   ├── router/
│   │   ├── store/
│   │   └── views/           # 各角色页面
│   └── vue.config.js
└── README.md
```

---

## API 说明（摘要）

- **认证**：`POST /api/auth/login`、`POST /api/auth/register`
- **公开**：`GET /api/pub/products`、`/pub/products/:id`、`/pub/products/new`、`/pub/products/rank`、`/pub/products/recommend?userId=`、`/pub/categories`、`/pub/news`、`/pub/reviews`
- **用户**：需携带 Header `Authorization: Bearer <token>`
  - 购物车：`/user/cart` 增删改查
  - 收藏：`/user/favorite`
  - 订单：`/user/order`、`/user/order/create`、`/user/order/:id/pay`（模拟支付）、`/user/order/:id/confirm-receive`
  - 评价：`POST /user/review`
- **商家**：`/merchant/product`、`/merchant/order`、`/merchant/order/:id/ship`、`/merchant/chat`、`/merchant/report/dashboard`
- **管理员**：`/admin/user`、`/admin/merchant`、`/admin/category`、`/admin/product`、`/admin/order`、`/admin/news`、`/admin/report/dashboard`

---

## 注意事项

1. 首次运行前请先创建 MySQL 数据库并修改 `application.yml` 中的账号密码。
2. 模拟支付仅用于演示，不会调用真实支付渠道。
3. 「猜您想买」依赖用户历史已收货订单数据，新用户或无订单时展示销量排行作为兜底。
