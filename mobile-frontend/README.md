# Easy Code - 移动端

独立移动端前端项目，与 Web 端（`frontend`）共用同一 Django 后端 API。

## 技术栈

- **Vue 3** + **TypeScript** + **Vite**
- **Vue Router** + **Pinia**
- **Vant 4**（移动端 UI 组件库）
- **Axios**（请求与鉴权）

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5174）
npm run dev
```

请确保后端已运行在 `http://127.0.0.1:8000`，Vite 会将 `/api`、`/user`、`/course` 等请求代理到后端。

## 构建

```bash
npm run build
```

产物在 `dist/`，可部署到任意静态托管或与后端同域提供。

## 目录说明

- `src/views/` - 页面（首页、课程、我的、登录、注册）
- `src/components/` - 可复用组件（按需添加）
- `src/router/` - 路由与鉴权
- `src/stores/` - Pinia 状态（如 auth）
- `src/services/` - API 封装（api、auth）
- `src/types/` - TypeScript 类型

## 后端对接

- **课程列表** `GET /api/courses/`
- **课程详情** `GET /api/course/<slug>/`（含视频、资料）
- **已购课程** `GET /api/purchased-courses/`
- **免费报名** `POST /check-out/<slug>/`
- **付费购买** `POST /api/create-checkout/<slug>/` → Stripe 结账，成功后 `POST /api/confirm-payment/`
- 支付成功/取消页：`/payment-success`、`/payment-cancelled`

若使用移动端端口（如 5175），需在 Django `settings.py` 中将 **`REDIRECT_DOMAIN`** 设为 `http://localhost:5175`，以便 Stripe 支付完成后跳回移动端。

## 与 Web 端区别

- 独立目录 `mobile-frontend`，不修改 `frontend`。
- 开发端口 **5174**（Web 为 5173），可同时跑两个前端。
- 使用 Vant 做移动端 UI，与 Web 端样式分离。
