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

---

## 📁 项目结构