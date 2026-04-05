# Java 基础

---

## 1. == 和 equals()

**答:** `==` 对基本类型比值，对引用类型比地址；`equals()` 默认比地址，但 String 等重写后比内容。

- 实际开发中判断逻辑相等用 `equals()`
- 重写 `equals()` 必须同时重写 `hashCode()`
- String 用 `==` 有时为 true 是因为字符串常量池

---

## 2. 重载 vs 重写

**答:** 重载是同一类中方法名相同、参数列表不同（编译期多态）；重写是子类重新实现父类方法（运行时多态）。

- 重载只看参数列表，和返回值无关
- `private` / `static` 方法不能被重写
- `static` 方法同名叫隐藏，不是重写

---

## 3. String / StringBuilder / StringBuffer

**答:** String 不可变；StringBuilder 可变、线程不安全、性能高；StringBuffer 可变、线程安全、性能低。

- 少量操作用 String，单线程频繁拼接用 StringBuilder，多线程共享拼接用 StringBuffer
- String 不可变的好处：线程安全、哈希值稳定、可利用常量池复用

---

## 4. 基本类型 vs 包装类型

**答:** 基本类型直接存值（int, long），包装类型是对象（Integer, Long），泛型和集合只能用包装类型。

- 包装类型可以表示 null，但拆箱时可能 NPE
- `Integer.valueOf()` 对 -128~127 有缓存，所以 `Integer a=127; Integer b=127; a==b` 为 true，128 则为 false

---

## 5. final / finally / finalize

**答:** final 修饰变量不可变、方法不可重写、类不可继承；finally 是异常处理代码块；finalize 是 GC 前回调方法（已废弃）。

- final 修饰引用：地址不能变，对象内容可能还能变
- finally 不一定 100% 执行（如 `System.exit()`、进程终止）

---

## 6. 接口 vs 抽象类

**答:** 接口偏规范和能力扩展（can-do），抽象类偏公共模板（is-a）；接口支持多实现，抽象类单继承。

- 抽象类可有成员变量、构造器、普通方法
- 接口可以有 `default` 和 `static` 方法（Java 8+）

---

## 7. 异常体系

**答:** `Throwable` → `Error`（系统级，如 OOM）+ `Exception`（程序级）；Exception 分 checked（编译期强制处理，如 IOException）和 unchecked（RuntimeException，如 NPE）。

- `throw` 是主动抛异常，`throws` 是方法签名声明可能抛的异常
- Error 通常不靠业务代码恢复

---

## 8. hashCode() 和 equals()

**答:** `equals()` 相等 → `hashCode()` 必须相等；`hashCode()` 相等 → `equals()` 不一定相等（哈希冲突）。

- 重写 `equals()` 不重写 `hashCode()` → HashSet 去重失效、HashMap key 查找异常

---

## 9. Java 参数传递

**答:** Java 只有值传递。传对象时传的是引用的副本，所以能改对象内部属性，但不能让外部引用指向新对象。

---

## 10. 深拷贝 vs 浅拷贝

**答:** 浅拷贝只复制对象本身，内部引用对象共用；深拷贝连内部引用也一起复制，副本完全独立。

- `Object.clone()` 默认浅拷贝
- 深拷贝实现：手动复制、拷贝构造器、序列化

---

## 11. 泛型

**答:** 把类型参数化，核心作用是编译期类型检查 + 减少强转。

- 泛型在运行期会类型擦除（Type Erasure）
- `List<int>` 不行，泛型只接收引用类型

---

## 12. 反射

**答:** 运行时动态获取类信息并操作类/对象。Spring 的依赖注入、AOP、注解处理底层都用了反射。

- 优点：灵活，框架必备
- 缺点：性能较差，破坏封装
