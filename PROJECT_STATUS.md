# CodeForge 项目交接文档（当前状态）

> 用途：换会话 / 换账号后，让新的 AI 会话读本文件即可无缝接手，不用重新摸清项目。
> 更新日期：2026-10-15

---

## 一、项目定位

**CodeForge 在线代码评测与技能考核平台**（个人项目）。两种模式：

- **公开训练**：用户选题 → 提交 Java 代码 → 异步判题 → 看反馈。
- **限时考核**：管理员创建考核、邀请码加入、时间窗口内答题、汇总结果（**尚未实现**）。

完整规划文档在项目根目录 `00~06` 共 7 份 md（当前账号曾上传，也可从 `D:\zhuomian\新建文件夹\CodeForge_..._项目文档\` 找到）。

---

## 二、技术栈（实际，非文档写的）

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot **4.0.8**（文档误写 3.x）、Java 17、Maven（`D:\maven\apache-maven-3.9.8\bin\mvn.cmd`） |
| 持久层 | MyBatis-Plus 3.5.15、MySQL 8 |
| 消息/判题 | RabbitMQ（重试+死信）、Docker CLI 沙箱（镜像 `codeforge-judge:1.0`） |
| 认证 | JWT（jjwt 0.12.3）、BCrypt、AOP `@RequireRole`、ThreadLocal `UserContext`、`TraceIdFilter` |
| 前端 | Vue3 + Vite、vue-router、axios（**无** Pinia / Element Plus / Monaco，用原生 textarea） |

> 项目**没有用 Flyway**（用户明确拒绝），数据库用「手写版本化 SQL 脚本 + 手动执行 + `CREATE TABLE IF NOT EXISTS`」。

---

## 三、目录结构

```
d:\zhuomian\codeforge-project\
├── codeforge\            # 后端（独立 git 仓库）
│   ├── src\main\java\com\codeforge\codeforge\
│   │   ├── common\       # Result/ErrorCode/异常/拦截器/AOP/UserContext
│   │   ├── user\         # 认证、用户
│   │   ├── problem\      # 题目、版本、测试用例
│   │   ├── submission\   # 提交
│   │   ├── judge\        # 判题（Docker 沙箱）
│   │   └── mq\           # JudgeListener / DeadLetterListener
│   ├── src\main\resources\application.yml / application-dev.yml
│   └── db\               # V1__init.sql / V2__problem_test_case.sql / V3__migrate_test_case.sql
├── codeforge-web\        # 前端（**非** git 仓库，未纳入版本控制）
│   └── src\components\   # ProblemList / ProblemDetail / ProblemForm / SubmissionDetail / CodeEditor 等
└── 00~06 文档.md + PROJECT_STATUS.md（本文件）
```

---

## 四、数据库（现库 codeforge，共 6 张表）

连接：`localhost:3306`，库 `codeforge`，`root / 1234`。

| 表 | 说明 |
|---|---|
| `sys_user` | id, username, password(BCrypt), email, **role(USER/AUTHOR/ADMIN)**, **status(1正常/0禁用)**, create_time, update_time |
| `problem` | id, title, difficulty(EASY/MEDIUM/HARD), tags(json), **status(DRAFT/PUBLISHED)**, creator_id, create_time, update_time, deleted(逻辑删除) |
| `problem_version` | id, problem_id, version, description, input_format, output_format, time_limit, memory_limit, **test_cases(json，已废弃不再读写)**, created_by, created_at |
| `problem_test_case` | id, **problem_version_id**, sort_no, input, output, **is_public(1公开/0隐藏)**, created_at |
| `submission` | id, problem_id, problem_version, user_id, code, language(JAVA), status, score, time_used, memory_used, error_message, submit_time, judge_time |
| `judge_fail_record` | id, submission_id, problem_id, user_id, fail_reason, retry_count, created_at |

- 建表脚本：`db/V1__init.sql`（快照）+ `db/V2__problem_test_case.sql`（用例表）+ `db/V3__migrate_test_case.sql`（旧 JSON→新表迁移，已执行）。
- `sys_user.status` 的语义是「1=正常，0=禁用」，默认值 `1`（已对齐）。
- 用例数据源在 `problem_test_case`，`problem_version.test_cases` JSON 列**仅保留作备份，待后续清理**。

---

## 五、环境与运行

| 项 | 值 |
|---|---|
| MySQL | 3306，库 `codeforge`，root/1234 |
| RabbitMQ | `127.0.0.1:15673`（**本机非标准映射**，guest/guest） |
| 判题镜像 | `codeforge-judge:1.0`（Docker） |
| 后端 | 8080（IntelliJ 启动 + devtools 热重启，或 `mvn spring-boot:run`） |
| 前端 | `npm run dev` → 5173，Vite proxy `/api` → `localhost:8080`（rewrite 去掉 `/api`） |

判题规则：容器里 `javac Solution.java` + `java Solution`，stdin 喂输入、stdout 比对；退出码 `0`=成功、`124`=TLE、非 0=RE；用 cgroup `memory.peak` 读峰值内存；`--memory` 取题目 memoryLimit。

---

## 六、已完成功能

1. 注册 / 登录（BCrypt + JWT + 账号禁用校验）。
2. 角色权限：USER / AUTHOR / ADMIN，`@RequireRole` 切面 + 资源归属校验（作者只能改自己的题）。
3. 题库 CRUD：列表分页、详情、版本化更新、逻辑删除。
4. 题目发布 / 下架：`PUT /problem/{id}/status`（**一段式** DRAFT↔PUBLISHED，作者/管理员可操作）。
5. **测试用例独立表 + 公开/隐藏**：`problem_test_case` + `is_public`；判题读**全部**用例，详情接口**按角色脱敏**（普通用户只见公开样例，作者/管理员见全量）。
6. Java 提交 + 异步判题：RabbitMQ（`judge.queue` + `dead.queue`），重试 3 次后进死信，写 `judge_fail_record`。
7. Docker 沙箱判题 + 耗时/内存回填（`timeUsed`/`memoryUsed`）。
8. 前端：登录/注册、题库列表/详情、编辑题目、代码提交、提交详情自动轮询刷新。
9. 判题 NPE 加固（版本/用例为空时返回 SYSTEM_ERROR）。

**实测已通过**：普通用户详情只返回公开用例、作者返回全量、隐藏用例参与判题计分（公开对隐藏错 = 50 分）。

---

## 七、功能缺口（对照规划文档）

### A. 完全缺失的核心模块
- **限时考核 assessment**（最大缺口）：`assessment` / `assessment_problem` / `assessment_participant` 表；邀请码、时间窗口校验、结果统计；前端「我的考核 + 考核详情倒计时」。
- **训练题单 training**：`training_set` / `training_set_problem` 表；题目编排 + 进度 + 前端页面。
- **Redis 缓存与限流**：`problem:detail` 缓存、`submit:limit` 提交频率、`judge:lock`。
- **批量重判 rejudge**：`rejudge_batch` / `rejudge_item` 表。

### B. 审核状态机不一致
- 当前：`DRAFT ↔ PUBLISHED`（一段式，作者可发布）。
- 文档：`DRAFT → PENDING_REVIEW → PUBLISHED → OFFLINE`，`REJECTED → DRAFT`；缺 `submit-review` 提交审核、`admin/publish` 审核发布、`REJECTED`/`OFFLINE` 状态。

### C. 判题可靠性与数据完整性
- `submission_case_result` 表欠缺（每次提交逐用例的结果/资源明细，目前只存总耗时总内存）。
- **Outbox**（`outbox_message`）缺失 —— 未覆盖「DB 已提交但 MQ 未发出」窗口。
- **MQ 幂等不足**：`submission` 无乐观锁 `version` + 条件更新（`status + version`），重复消息可能重复计分。
- 定时恢复长时间 `RUNNING` 任务缺失；死信人工恢复入口（查询/重试 `judge_fail_record`）缺失。
- 提交长度 / 频率校验缺失；Docker 残留资源无定时兜底清理。

### D. 前端
- 缺 Pinia / Element Plus / Monaco Editor / SSE。
- 缺「我的提交」历史列表页、训练题单页、考核页。
- 路由守卫未做；页面 loading/empty/error/permission denied 状态未系统化。
- **CodeEditor 语言选项是假选项**：前端给 JAVA/PYTHON/CPP，后端只支持 `javac/java Solution`，选 Python/C++ 必 CE。

### E. 工程化 / 可观测性
- docker-compose、README、接口文档、Testcontainers 集成测试均缺失。
- 可观测指标（队列积压/排队耗时/状态分布/重试死信数）缺失。
- 管理员操作审计日志、异常账号管理缺失。

---

## 八、待定的业务口径（重要，动手前先定）

1. **发布一段式 vs 两段式**：现在作者直接发布；文档要求作者提交审核 → 管理员发布。
2. `problem.status` 的终态命名：代码/DB 用 `ARCHIVED`，文档用 `OFFLINE`。
3. 文档技术栈（SB3 + Flyway）与实际（SB4.0.8 + 手写 SQL）不一致（用户已明确拒绝 Flyway）。
4. 接口路径：文档 `/api/problems`（复数+prefix），实际 `/problem`（单数+无前缀）。
5. 角色口径：文档 00 说第一版只有 USER/ADMIN，实际三种角色同时存在。
6. `submission` 字段命名：文档 `created_at`，实际 `submit_time`。

---

## 九、下一步建议顺序（对齐文档路线）

1. **Redis 缓存与限流**（成本小、见效快）
2. **判题一致性加固**（幂等 + Outbox + 恢复任务 + 重判 + `submission_case_result`）
3. **审核状态机对齐**（两段式）
4. **训练题单**
5. **限时考核**（最后）

---

## 十、Git 与部署

- 后端 `codeforge/` 是独立 git 仓库，分支 `main`，已推两个远端：
  - `origin` → `https://gitee.com/wangcj0407/codeforge.git`
  - `github` → `https://github.com/wcj0407/codeforge.git`（已清理多余 pushurl）
- 前端 `codeforge-web/` **未纳入 git**。根目录 7 份文档也未纳入 git。
- 本仓库配置了 `http.proxy = 127.0.0.1:7890`（Clash）。推 GitHub 时需开 Clash，或临时 `git -c http.proxy= -c https.proxy= push github main`。
- `application-dev.yml` 含 dev 凭据（root/1234、guest/guest）已被提交，公开仓库需注意。