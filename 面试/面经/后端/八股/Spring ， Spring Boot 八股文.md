# Spring / Spring Boot

---

## 1. Spring 是什么

**答:** 轻量级 Java 框架，核心价值是管理对象、管理依赖、降低耦合。提供 IoC、AOP、事务、MVC 等能力。

---

## 2. IoC / DI

**答:** IoC（控制反转）= 对象创建和管理权交给 Spring 容器，不再手动 new。DI（依赖注入）是 IoC 的具体实现，容器自动把依赖对象注入进去。

- IoC 是思想，DI 是实现

---

## 3. AOP

**答:** 面向切面编程，把日志、事务、权限等横切逻辑从业务代码抽出来统一处理。

- 底层：动态代理。有接口用 JDK 动态代理，无接口用 CGLIB（生成子类）
- final 类不能被 CGLIB 代理（不能继承）

---

## 4. Bean 生命周期

**答:** 实例化 → 依赖注入 → 初始化（@PostConstruct）→ 使用 → 销毁（@PreDestroy）

---

## 5. 常用注解

| 注解 | 作用 |
|------|------|
| @Component | 通用组件，交给 Spring 管理 |
| @Service / @Repository | 业务层 / 持久层（语义分层） |
| @RestController | 接口层，= @Controller + @ResponseBody |
| @Autowired | 自动注入依赖（推荐构造器注入） |

---

## 6. Spring MVC 执行流程

**答:** 请求 → DispatcherServlet（前端控制器）→ HandlerMapping 找 Controller → HandlerAdapter 执行 → 返回结果 → 响应客户端

- 核心：DispatcherServlet 统一接收、分发、返回

---

## 7. @SpringBootApplication

**答:** 组合注解 = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan

- 配置类 + 自动配置 + 组件扫描

---

## 8. Spring Boot 自动配置

**答:** 根据引入的依赖和环境，按条件（@Conditional）自动装配 Bean。约定大于配置。

- 入口：@EnableAutoConfiguration
- 不是无脑全配，而是按条件装配

---

## 9. @Transactional

**答:** 声明式事务，底层本质是 AOP 代理。方法正常则提交，异常则回滚。

### 默认回滚规则

- 运行时异常 RuntimeException / Error → 回滚
- 受检异常 checked exception → 默认不回滚，需显式 `rollbackFor = Exception.class`

### 事务失效场景（高频必背）

| 场景 | 原因 |
|------|------|
| 同类内部调用 | this.xxx() 绕过代理 |
| 异常被 catch 吞掉 | Spring 感知不到异常 |
| 方法非 public | 代理通常针对公开方法 |
| 抛出受检异常 | 不在默认回滚范围 |
| 对象非 Spring Bean | 自己 new 的对象无代理 |

### 事务传播行为

- **REQUIRED（默认）:** 有事务就加入，没有就新建
- **REQUIRES_NEW:** 强制新开事务

---

## 10. JDK 动态代理 vs CGLIB

| 对比 | JDK 动态代理 | CGLIB |
|------|-------------|-------|
| 基础 | 基于接口 | 基于继承（生成子类） |
| 要求 | 目标类须实现接口 | 无需接口 |
| 限制 | — | final 类/方法不能代理 |

---

## 11. BeanFactory vs ApplicationContext

**答:** BeanFactory 最基础的容器接口；ApplicationContext 是增强版，支持国际化、事件、资源加载等。实际开发用 ApplicationContext。

---

## 12. Spring vs Spring Boot

**答:** Spring 提供核心能力（IoC/AOP/事务/MVC），Spring Boot 在此基础上通过自动配置 + Starter + 内嵌服务器简化配置、快速开发。

- Starter = 场景化依赖套餐
