# 🧩 AI云图库

> 一个基于 **Vue3 + TailwindCSS + TypeScript + Ant Design Vue** 的前端项目  
> 搭配 **Spring Boot 2.7 + MyBatis-Plus + Redis + MySQL** 的后端系统，  
> 实现前后端分离的现代化 Web 应用模板。

---

## 🚀 项目简介

### 用户模块

* 用户注册
* 用户登录
* 用户注销（退出登录）
* 用户管理



### 图片模块

* 图片上传
* 图片管理
* 图片编辑
* 批量上传图片



### 图片优化

* 增加 Redis 查询缓存
* 增加缩略图：图片转成 webp格式（非webp格式的文件触发）
* 删除图片时，关联删除对象存储文件
* 图片点赞功能

---

## 🏗️ 技术栈

### 前端（Frontend）
| 技术                                                         | 说明                   |
| ------------------------------------------------------------ | ---------------------- |
| [Vue3](https://vuejs.org/)                                   | 渐进式 JavaScript 框架 |
| [TypeScript](https://www.typescriptlang.org/)                | 类型安全支持           |
| [Vite](https://vitejs.dev/)                                  | 极速构建工具           |
| [Ant Design Vue](https://www.antdv.com/docs/vue/introduce-cn/) | UI 组件库              |
| [TailwindCSS](https://tailwindcss.com/)                      | 原子化 CSS 框架        |
| [Axios](https://axios-http.com/)                             | HTTP 请求库            |

### 后端（Backend）
| 技术                                                      | 说明                |
| --------------------------------------------------------- | ------------------- |
| [Spring Boot 2.7](https://spring.io/projects/spring-boot) | 后端框架核心        |
| [Java 11](https://openjdk.org/projects/jdk/11/)           | 项目语言            |
| [MyBatis-Plus](https://baomidou.com/)                     | ORM 框架，简化 CRUD |
| [Redis](https://redis.io/)                                | 分布式缓存          |
| [MySQL](https://www.mysql.com/)                           | 关系型数据库        |
| [Lombok](https://projectlombok.org/)                      | 减少样板代码        |
| [Hutool](https://hutool.cn/docs/)                         | Java 工具库         |

***

# 系统模块

### 用户模块



### 公共图库模块（图片模块）

1. 管理员管理图片
2. 用户上传图片
3. 管理员审核用户上传的图片
4. 批量爬取图片



### 私用空间模块

1. 管理员能管理空间
2. 用户创建私用空间
3. 私有空间权限控制
4. 空间级别和限额控制



#### 后端开发

除了基本的空间增删改查外，需要对原先的图片模块（图片表）增加一个字段: `spaceId`,

1. 上传图片，需要增加对 `spaceId` 的校验，如果不会空，需要判断空间是否存在
   1. 存在，校验是否有该空间的权限，校验空间容量和大小是否充足
2. 更新图片：更新前的图片为空间 A，更新后的图片为空间 B，不允许这种情况
   1. `spaceId` 为空，并且旧的图片 `spaceId` 不为空，则使用旧的 `spaceId` 
   2. `spaceId` 不为空，检验跟之前是否一直，不一致，直接报错
3. 上传图片的路径修改
4. 修改和删除的图片权限需要和之前的进行区分，
   1. 公共图库（`spaceId == null`），仅本人和管理员可删除和修改
   2. 私有空间（`spaceId != null`）, 仅空间创建者可删除和修改
5. 查询图片权限修改：
   1. 根据id 查询 vo:  `spaceId != null` 时，校验权限
   2. 分页查询图片 vo:  增加 `spaceId` 查询字段支持，允许传递 `spaceId`
      1. `spaceId == null` ，数据库拼接条件不会增加该字段的支持，导致查出所有图片，所以需要 再增加一个字段 `nullSpaceId`        `boolean` 类型，表示是否只查询 `spaceId`  为 `null` 的数据
      2. `spaceId != null`， 正常添加查询条件
   3. 分页查询VO 权限校验：
      1. `spaceId == null` : 仅查询审核通过的图片，同时设置字段 `nullSpaceId` 为 `true`
      2. `sapceId != null` : 判断空间是否存在；判断空间的创建者是不是当前的登录用户
6. 上传图片前校验空间大小和容量；上传图片后，更新空间大小和容量
7. 删除图片释放空间额度
8. 删除空间时，清理掉空间中的图片，同时删除对象存储中的图片



#### 前端开发

1. 空间管理页面
   1. 新建页面，添加到菜单和路由
   2. 编写相关逻辑，调用 `opeanapi` 生成的接口

2. 创建空间页面（给用户创建空间使用，管理员可以额外选择空间级别）
   1. 复制 `AddPicturePage` 页面，增加 路由
   2. 页面支持添加和修改，两种功能
3. 展示我的空间页面
   1. 增加侧边栏选择，定义全局侧边栏组件
   2. 添加到 `BasicLayout` 中
   3. 当用户为登录时，侧边栏只有一个公共图库，用户登录之后，再额外增加我的空间展示
      1. 点击我的空间，未登录跳转到登录页面
      2. 如果还没有创建过空间，则跳转到空间创建页面
      3. 如果已经有空间，则跳转到我的空间页面
4. 新增 我的空间页面
   1. **这个页面只作为一个中间人，在这个页面去跳转到空间详情页面**

5. 新增 空间详情页面
   1. 展示我的空间中的图片和空间使用情况
      1. 抽离出首页的图片列表，增加一个图片列表组件
      2. 复制一份 `PictureDetailPage.vue` 在上面进行修改
      3. 增加一个按钮：上传图片，点击跳转到上传图片页面，同时表示上传到该空间中，需要通过url 传递 spaceId
      4. 空间详情的图片列表展示增加编辑和删除按钮，注意阻止事件冒泡
      5. 点击编辑需要把空间id传递过去到图片详情页
6. 图片管理页面：增加只能查公共图库的图片



### 图片功能扩展

#### 搜索能力增强

##### 以图搜图

1. 调用百度以图搜图接口，
2. 该接口返回一个url,使用jsoup 解析提取里面javascript 内容 里面有firstUrl
3. 调用 fisrtUrl 返回图片列表
4. 把这3步封装成一个门面类进行调用

#### 以颜色搜图

使用 腾讯云对象存储 cos 提供的数据万象能力，上传图片时，增加参数，让其返回图片的主色调

保存数据库时，将主色调一并保存

由于数据库万象返回的是颜色的16进制，16进制会进行压缩，导致一些位数缺失，需要进行修复，才能在前端展示出主色调

前端还需要引入新的组件，方便颜色选择

#### 图片分享功能

前端增加模态框，点击分享弹窗，可以url 和 二维码分享

#### 批量编辑空间图片

支持批量设置空间名称、分类、标签



# 图库分析

分三个维度的分析：全空间分析，公共图库分析，某个用户的空间分析

分析类别：

* 空间资源使用情况分析
* 空间图片分类分析
* 空间图片大小分析
* 用户上传行为分析
* 空间使用排行榜



每种类别都可以按三个维度进行查询，处了空间使用排行

所以每个查询的dto可以继承通用的查询参数dto



前端引入 ECharts、vue-ECharts、词云库

编写相关分析页面

我的空间页面增加空间分析按钮

空间管理页面，增加全图库分析按钮，公共图库分析按钮



### 团队空间

复用现有的空间表，增加一个 空间类型 字段，用于区分是私有空间还是公共空间

为了不影响现有代码，创建私有空间是，由于没有传递空间类型，默认填充参数 为私有空间，即使数据库加了default 默认值，也显示的设置一下。这样也方便统一参数校验，**先填充默认参数（为空时填充），创建空间时，校验是否传递了空间类型。**

***一个用户只能创建一个私有空间和一个团队空间***



团队空间是有多个成员的，一对多的关系，同时一个用户也可以加多个空间，也是一对多的关系。

组合起来就是多对多，所以需要新建一张 space_user 表 存储 空间和用户的关系。

创建团队空间时，需要把创建者的信息也作为一条记录添加到 space_user 表中。



对 space_user 进行增删改查，完成以下接口：

* 增：添加成员到空间：仅拥有成员管理权限的用户可以使用
* 删：从空间移除成员：仅拥有成员管理权限的用户可以使用
* 查：查询某个成员在空间中的信息：仅拥有成员管理权限的用户可以使用
* 查：查询空间成员列表：仅拥有成员管理权限的用户可以使用
* 改：编辑成员信息（例如修改成员角色）：仅拥有成员管理权限的用户可以使用
* 查询我加入的团队空间列表：所有已登录的用户都可以使用



#### 邀请用户加入空间和审批

新增邀请状态：`0-待确认 / 1-已接受 / 2-已拒绝`

新增邀请人字段 `createUserId`

仅已接受邀请的空间管理员可以邀请成员

受邀用户只能审批自己的邀请

拒绝后支持管理员重新邀请

使用条件更新避免并发重复审批

“我的团队空间”仅返回已接受的空间

新增接口：

- `POST /spaceUser/invite/list/my`：查询待确认邀请
- `POST /spaceUser/invite/review`：接受或拒绝邀请

团队空间创建者自动设置为已接受的管理员



### 空间权限控制

RBAC 权限控制，上面提到的接口是需要权限的，定义不同的角色，不同的角色拥有不同的权限

引入 `sa-token` 进行鉴权

为了减少代码的修改，原本对 user 表的鉴权保持不变，继续使用，使用 `sa-token` 鉴权空间成员权限。利用多账号认证特征，将多套账号的认证区分开，让他们互不干扰。

使用https://sa-token.cc/doc.html#/up/many-account 多账号认证实现



引入 多账号登录认证时，我们要在登录时，在这套账号体系下也要登录，修改 登录service 的实现逻辑，补充这段内容。



接着就是鉴权，我们如何知道一个用户是否有权限呢，两种方式：获取该用户所具有的角色，或者直接获取该用户的权限列表。通过这两个中的一个，我们就可以进行判断。



根据文档：https://sa-token.cc/doc.html#/use/jur-auth

我们需要编写一个类：获取当前账号权限码

```java
/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展 
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();    
        list.add("101");
        list.add("user.add");
        list.add("user.update");
        list.add("user.get");
        // list.add("user.delete");
        list.add("art.*");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();    
        list.add("admin");
        list.add("super-admin");
        return list;
    }

}

```



我们要给空间图片和空间成员的操作进行鉴权，但是 `StpInterface#getPermissionList` 默认只能拿到登录用户 ID 和 `loginType`，不知道当前操作的是哪张图片、哪个空间或哪条成员关系。因此项目定义了统一的 `SpaceUserAuthContext`，用于在业务代码和 Sa-Token 权限加载器之间传递 `Picture`、`Space`、`SpaceUser` 等业务对象。

项目早期使用注解式鉴权：拦截器在 Controller 方法执行前读取 URL 和请求参数，再根据参数中的 ID 查询业务对象。由于真正的业务方法随后还会查询同一个对象，这会造成重复查库；JSON 请求还需要使用 `RequestWrapper` 缓存请求体，产生额外的内存和处理开销。因此当前实现已改为基于 ThreadLocal 上下文的编程式鉴权。



#### 为什么有 `spaceUserId` 时要查询两次 `space_user`

先区分两个容易混淆的概念：

- `spaceUserId`：请求中携带的 `space_user.id`，表示**被操作的成员关系**。例如管理员编辑或删除某个成员时，这个 ID 属于目标成员。
- `userId`：从 Sa-Token 登录会话中取得的用户 ID，表示**发起请求的当前登录用户（操作者）**。

例如，用户 A 是团队空间 100 的管理员，要把用户 B 修改为编辑者：

```text
当前登录用户：A（userId = 1）
请求参数 id：20（spaceUserId = 20，对应用户 B 在空间 100 的成员记录）
```

鉴权过程如下：

```text
spaceUserId = 20
    │
    ├─ 第一次查询：按 space_user.id 查目标成员 B
    │              目的：得到本次操作所属的 spaceId = 100
    │
    └─ 第二次查询：按 userId = 1、spaceId = 100 查操作者 A
                   目的：得到 A 在空间 100 中的角色，再映射为权限列表
```

因此两次查询不是在查询同一个人：第一次查询的是**操作目标**，第二次查询的是**操作主体**。权限必须取自当前操作者的成员记录。

不能直接使用第一次查询得到的角色。否则，系统判断的会是“被操作成员拥有什么权限”，而不是“当前登录用户能不能操作他”。例如普通成员去编辑一个管理员的记录时，若直接使用目标管理员的角色，反而会错误地获得管理员权限，造成越权。

当前实现还需要满足以下约束：

1. 第二次查询只认可 `inviteStatus = 1`（已接受）的成员。待确认或已拒绝邀请的用户不能获得空间权限。
2. 数据库通过唯一索引 `uk_spaceId_userId (spaceId, userId)` 保证一个用户在同一空间最多只有一条成员记录，所以这里可以使用 `one()`。
3. 查不到目标成员时返回“未找到空间用户信息”；查不到操作者的已接受成员记录时返回空权限列表，由 Sa-Token 拒绝本次操作。
4. 角色只能信任数据库查询结果，不能直接使用请求体反序列化出的 `spaceUser.spaceRole`。请求数据由客户端控制，如果客户端伪造 `spaceRole = admin` 而服务端直接据此生成权限，会造成越权。

只有当接口明确保证 `spaceUserId` 永远是当前登录用户自己的成员记录，并且已校验记录中的 `userId` 等于登录用户 ID 时，才可能复用第一次查询结果。当前的成员编辑、删除等接口操作的是其他成员，不能这样省略第二次查询。



#### 从注解式鉴权改为编程式鉴权

##### 为什么仅增加 ThreadLocal 还不够

注解鉴权发生在 Controller 方法执行前，业务查询尚未执行。此时即使引入 ThreadLocal，也没有已经查询出的 `Picture`、`Space` 或 `SpaceUser` 可以复用，所以仍然需要在权限加载器中查询一次数据库，进入业务方法后再查询一次。

当前实现调整了执行顺序：

```text
Controller / Service 查询业务对象
              │
              ▼
将已查询对象写入 SpaceUserAuthContext
              │
              ▼
SpaceUserAuthManager 执行编程式权限校验
              │
              ▼
StpInterfaceImpl 从 ThreadLocal 取得同一个对象
              │
              ▼
只查询当前操作者的团队成员关系，映射出权限列表
              │
              ▼
finally 清理 ThreadLocal，继续执行后续业务
```

这样才能真正复用业务查询结果。例如删除图片时，业务代码查出的 `oldPicture` 会直接提供给权限加载器，不再根据请求参数重复查询图片。

##### ThreadLocal 上下文

`SaTokenContextHolder` 保存当前同步请求正在鉴权的业务上下文：

```java
public final class SaTokenContextHolder {

    private static final ThreadLocal<SpaceUserAuthContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(SpaceUserAuthContext authContext) {
        if (authContext == null) {
            clear();
            return;
        }
        CONTEXT.set(authContext);
    }

    public static SpaceUserAuthContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

`SpaceUserAuthContext` 提供了 `ofPicture`、`ofSpace`、`ofSpaceUser`、`ofPictureAndSpace` 等工厂方法，业务代码可以按当前操作传入已经查出的对象。

##### 统一的编程式鉴权入口

`SpaceUserAuthManager` 负责设置上下文、调用 Sa-Token 并清理上下文：

```java
public void checkPermission(String permission, SpaceUserAuthContext authContext) {
    SaTokenContextHolder.setContext(authContext);
    try {
        StpKit.SPACE.checkPermission(permission);
    } finally {
        SaTokenContextHolder.clear();
    }
}
```

对于既要校验权限、又要把完整权限列表返回前端的场景，可以调用：

```java
public List<String> getPermissionList(SpaceUserAuthContext authContext) {
    SaTokenContextHolder.setContext(authContext);
    try {
        return StpKit.SPACE.getPermissionList();
    } finally {
        SaTokenContextHolder.clear();
    }
}
```

##### 业务代码使用示例

删除图片时先完成业务查询，再使用同一个 `Picture` 对象鉴权：

```java
Picture oldPicture = this.getById(id);
ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);

spaceUserAuthManager.checkPermission(
        SpaceUserPermissionConstant.PICTURE_DELETE,
        SpaceUserAuthContext.ofPicture(oldPicture)
);

this.removeById(id);
```

删除空间成员时，Controller 已经查询了目标成员和所属空间，可以同时传入两个对象：

```java
SpaceUser oldSpaceUser = spaceUserService.getById(id);
Space space = spaceService.getById(oldSpaceUser.getSpaceId());

spaceUserAuthManager.checkPermission(
        SpaceUserPermissionConstant.SPACE_USER_MANAGE,
        SpaceUserAuthContext.ofSpaceUserAndSpace(oldSpaceUser, space)
);
```

##### 权限加载器如何复用对象

`StpInterfaceImpl#getAuthContextByRequest` 的名称暂时保留，但不再读取 HTTP 请求，而是直接读取 ThreadLocal：

```java
private SpaceUserAuthContext getAuthContextByRequest() {
    return SaTokenContextHolder.getContext();
}
```

权限加载器优先使用上下文中的完整对象：

- 存在 `Picture` 时，直接取得图片的 `spaceId` 和 `userId`。
- 存在 `Space` 时，不再根据 `spaceId` 重复查询空间。
- 存在 `SpaceUser` 时，将它视为被操作的目标成员，并据此确定所属空间。
- 只有上下文仅提供 ID、没有完整对象时，才回退查询数据库。
- 没有传入任何鉴权上下文时返回空权限列表，采用失败关闭策略，不能默认授予管理员权限。

对于团队空间，即使已经复用了目标对象，仍然必须查询“当前登录用户在该空间中的成员关系”。这次查询和目标对象查询含义不同，不能省略，也不能把目标成员的角色当成操作者角色。查询时只认可 `inviteStatus = 1` 的已接受成员。

##### 清理与使用限制

ThreadLocal 在线程池中必须清理，否则线程复用后可能把上一个请求的鉴权对象带到下一个请求。当前实现有两层清理：

1. `SpaceUserAuthManager` 在每次权限校验的 `finally` 中立即清理。
2. `HttpRequestWrapperFilter` 在整个请求结束的 `finally` 中兜底清理。

该方案适用于当前 Spring MVC 的同步请求模型。不要把 ThreadLocal 上下文直接传入 `@Async`、异步线程池或响应式流水线；异步任务需要显式传递鉴权所需对象，并在对应线程中独立设置和清理上下文。

完成改造后，图片上传、删除、编辑、按颜色搜索、批量编辑，以及空间成员查询、删除、编辑等接口不再依赖 `@SaSpaceCheckPermission` 前置解析请求。JSON 请求也不再需要为了鉴权而缓存并重复读取请求体。

#### 全局异常处理器增加异常类型拦截

查看图片详情额外返回权限列表，方便前端展示相关按钮
