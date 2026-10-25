# CodeForge 在线代码评测平台

> 仓库地址：https://github.com/wcj0407/codeforge-platform

一个基于 **Spring Boot 4 + Vue 3** 的在线判题系统：用户浏览题目、提交 Java 代码，后端通过 RabbitMQ 异步分发判题任务，在 Docker 沙箱中编译运行，并把结果回写数据库。

适合作为后端 / 全栈方向的个人项目，重点在**判题链路可靠性**和**多角色权限**，而不是堆页面。

---

## 功能概览

| 模块 | 能力 |
|---|---|
| 认证 | 注册（用户名 + 邮箱 + 密码）、登录、JWT、BCrypt、账号禁用 |
| 找回密码 | 邮箱验证码重置密码；管理员重置临时密码兜底 |
| 个人中心 | 修改用户名 / 密码 / 邮箱、上传头像（MinIO）、刷题统计 |
| 题库 | 列表、关键词搜索、难度筛选、题目详情、公开样例展示 |
| 出题 | 创建 / 编辑题目、题目版本化、公开 / 隐藏用例、提交审核、下架 |
| 审核 | 管理员通过 / 驳回题目，驳回必须填写原因 |
| 判题 | Java 提交、RabbitMQ 异步判题、Docker 沙箱、AC / WA / TLE / CE / RE |
| 提交记录 | 我的提交列表、判题状态轮询、代码与错误信息查看 |
| 管理员 | 用户列表、禁用 / 启用、角色调整、重置密码、查看任意用户提交 |
| 题单 | 创建题单、公开 / 私有、题目编排 |
| 考核 | 创建考核、题目列表、排名（时间窗校验尚未实现） |

---

## 架构

```
Vue 3 + Element Plus (5173)
        │  /api  (Vite proxy)
        ▼
Spring Boot 4 (8080)
        ├── MySQL 8              用户 / 题目 / 题目版本 / 用例 / 提交 / 题单 / 考核
        ├── Outbox 表 + 定时任务
        │        └── RabbitMQ ── judge.queue ──► JudgeListener
        │                              └── 失败重试 3 次 ──► dead.queue
        ├── Docker 沙箱           codeforge-judge:1.0，javac + java，限时 / 限内存
        └── MinIO                 头像存储（bucket 自动创建并设置公开读）
```

判题幂等做了两层：

1. 终态提交（AC / WA / TLE / CE / RE）再次收到消息直接返回已有结果；
2. `UPDATE ... WHERE status IN ('PENDING','SYSTEM_ERROR')` 原子抢占判题权，并发重复消息只有一个能进入 Docker。

---

## 技术栈

- **后端**：Java 17、Spring Boot 4.0.8、MyBatis-Plus 3.5.15、MySQL 8
- **消息**：RabbitMQ（手动 ACK、重试、死信队列）、Outbox 模式
- **判题**：Docker CLI 沙箱、cgroup 内存统计
- **认证**：JWT（jjwt）、BCrypt、AOP `@RequireRole`、ThreadLocal `UserContext`
- **存储**：MinIO（头像）、Spring Mail（找回密码）
- **前端**：Vue 3、Vite、Vue Router、Pinia、Element Plus、Axios
- **测试**：JUnit 5、Mockito、真实 Docker 沙箱测试

---

## 目录结构

```
codeforge-project/
├── codeforge/                     # 后端
│   ├── src/main/java/com/codeforge/codeforge/
│   │   ├── common/                # Result、ErrorCode、JWT、AOP、拦截器
│   │   ├── user/                  # 认证、用户、管理员
│   │   ├── problem/               # 题目、版本、测试用例、审核
│   │   ├── submission/            # 提交、权限、Outbox 写入
│   │   ├── judge/                 # Docker 判题执行器
│   │   ├── mq/                    # 判题消费者、死信消费者
│   │   ├── problemset/            # 题单
│   │   └── exam/                  # 考核
│   ├── src/main/resources/
│   │   ├── application.yml        # 公共配置，导入 application-local.yml
│   │   ├── application-dev.yml    # 开发环境配置
│   │   └── application-local.example.yml
│   ├── src/test/java/             # 24 个核心测试
│   ├── db/V1 ~ V10__*.sql         # 手写版本化数据库脚本
│   └── judge-sandbox/Dockerfile   # 判题镜像
├── codeforge-web/                 # 前端
├── docker/mysql-init.sh           # 按版本号顺序执行数据库脚本
├── docker-compose.yml             # MySQL + RabbitMQ + MinIO
├── .env.example
└── README.md
```

---

## 快速启动

### 0. 环境要求

| 依赖 | 版本 |
|---|---|
| JDK | 17 |
| Maven | 3.9+ |
| Node.js | 18+ |
| Docker Desktop | 24+（需要能执行 `docker` 命令） |
| MySQL / RabbitMQ / MinIO | 由 docker compose 提供 |

### 1. 启动基础设施

在项目根目录执行：

```bash
# 可选：自定义端口和密码
cp .env.example .env

docker compose up -d
docker compose ps
```

`.env` 里是本地密码，不要提交到仓库。

启动内容：

| 服务 | 地址 | 默认账号 |
|---|---|---|
| MySQL | `localhost:3306` | `root / 1234` |
| RabbitMQ | `localhost:5672`，管理台 `localhost:15672` | `codeforge / codeforge123` |
| MinIO | API `localhost:9000`，控制台 `localhost:9001` | `codeforge / codeforge123456` |

MySQL 第一次启动时会自动按 `V1 → V10` 的顺序执行 `codeforge/db` 下的脚本，建表并导入示例题库。

> 只有数据卷为空时才会执行初始化。需要重新初始化时执行 `docker compose down -v`（会清空数据）。

### 2. 构建判题镜像

```bash
docker build -t codeforge-judge:1.0 ./codeforge/judge-sandbox
```

### 3. 配置并启动后端

先复制本地配置文件：

```bash
cd codeforge/src/main/resources
cp application-local.example.yml application-local.yml
```

`application-local.yml` 已被 `.gitignore` 忽略，按需修改：

- RabbitMQ 指向 compose 的 `5672` + `codeforge` 账号；
- MinIO 指向 `http://127.0.0.1:9000`；
- 邮件填写自己的 SMTP 账号（QQ 邮箱用授权码，不是登录密码）。

启动后端：

```bash
cd codeforge
mvn spring-boot:run
```

也可以直接在 IntelliJ 运行 `CodeforgeApplication`。

后端启动成功后可访问 `http://localhost:8080`。

### 4. 启动前端

```bash
cd codeforge-web
npm install
npm run dev
```

打开 `http://localhost:5173`。

### 5. 创建管理员账号

系统不内置演示账号。先在页面注册一个账号，然后把它提升为管理员：

```bash
docker exec -it codeforge-mysql mysql -uroot -p1234 codeforge \
  -e "UPDATE sys_user SET role='ADMIN' WHERE username='你的用户名';"
```

重新登录后导航栏会出现「审核」和「用户管理」。

### 6. 验证判题链路

1. 用管理员把任意一道题确认在「已发布」状态；
2. 用普通账号进入题目详情，选择 Java；
3. 提交一段读取两个整数并输出和的代码（题目需要匹配输入输出格式）；
4. 几秒后提交详情应显示 `ACCEPTED`，并带耗时和内存。

---

## 测试

```bash
cd codeforge
mvn test -Dtest=JudgeIdempotencyTest,JudgeListenerTest,DockerJudgeExecutorTest,RoleAspectTest,SubmissionPermissionTest
```

覆盖内容：

| 测试类 | 数量 | 覆盖点 |
|---|---|---|
| `DockerJudgeExecutorTest` | 6 | AC、WA、CE、RE、TLE、Docker 内存限制 |
| `RoleAspectTest` | 6 | 未登录、USER / AUTHOR 越权、ADMIN / AUTHOR 放行 |
| `SubmissionPermissionTest` | 3 | 本人、他人、管理员查看提交 |
| `JudgeIdempotencyTest` | 5 | 终态跳过、原子抢占、重复投递只判一次 |
| `JudgeListenerTest` | 4 | ACK、重试头、死信 |

共 24 个测试。Docker 相关测试需要本机存在 `codeforge-judge:1.0` 镜像，缺失时会自动跳过。

`CodeforgeApplicationTests` 会启动完整上下文，运行它需要 MySQL / RabbitMQ / MinIO 都已就绪。

---

## 关键设计说明

**为什么用 Outbox**  
创建提交和写 MQ 消息不是同一个事务。先把 `JUDGE_SUBMISSION` 事件写到 `outbox_event`，再由定时任务投递，避免「数据库已提交但消息没发出去」。

**为什么判题结果要条件更新**  
RabbitMQ 是至少一次投递。消费者先通过 `status IN ('PENDING','SYSTEM_ERROR')` 原子抢占，只有抢到的消费者才启动 Docker，避免重复判题和重复覆盖结果。

**测试用例为什么分公开 / 隐藏**  
普通用户只能看到 `is_public = 1` 的样例；作者和管理员可见全量；判题始终读取全量用例。

**题目为什么版本化**  
每次编辑生成新的 `problem_version`，提交记录绑定当时的版本，历史提交不会因为题目后来被修改而失效。

---

## 常见问题

**RabbitMQ 连不上**  
compose 的默认用户是 `codeforge`，端口 `5672`；仓库里的 `application-dev.yml` 开发默认是 `15673 / guest`。确认 `application-local.yml` 已按示例覆盖这两项。

**MinIO 头像上传失败**  
确认 `application-local.yml` 的 endpoint、access-key、secret-key 与 compose 一致，并且后端启动日志里没有 MinIO 连接告警。bucket 会在后端启动时自动创建。

**MySQL 没有建表**  
初始化只在数据卷为空时执行。执行 `docker compose down -v` 后重新 `up`。

**判题一直是 PENDING**  
检查 RabbitMQ 是否正常、后端控制台是否有 `outbox` 投递异常，以及 Docker 是否在运行、`codeforge-judge:1.0` 镜像是否存在。

**端口冲突**  
修改 `.env` 中的 `MYSQL_PORT`、`RABBITMQ_PORT`、`MINIO_API_PORT`，并同步修改 `application-local.yml`。

---

## 后续计划

- 考核时间窗与参与校验（当前提交接口不校验考核时间）
- 密码修改后旧 token 失效
- 逐用例判题结果（`submission_case_result`）
- 重判入口、长时间 RUNNING 的恢复任务
- 管理员操作审计日志
- 接口文档（springdoc-openapi）与 CI
