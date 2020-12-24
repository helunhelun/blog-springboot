## 1、下载代码

```shell
git clone https://github.com/helunhelun/blog_sprinigboot.git
```

## 2、新建数据库

<p style="font-size:20px;">新建数据库用户名</p>

```shell
# 如果你想任意ip能访问，可以更换为 % ，password为你的设定的密码
CREATE USER 'test'@'localhost' IDENTIFIED BY 'test@123';
```

<p style="font-size:20px;">创建一个数据库并且赋予用户权限</p>

```shell
# blog_db为数据库名
CREATE DATABASE blog_db DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
# 开通权限 test为你的用户名，localhost为主机 test@123为用户密码
grant all privileges on blog_db.* to test@localhost identified by 'test@123';
```

<p style="font-size:20px;">在application.properties文件中修改连接数据库的配置</p>

```yaml
# 数据库配置
#Mysql属性配置文件,Spring-boot系统配置
spring.application.name=jpa-demo
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/blog_db?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2b8
spring.datasource.username=test
spring.datasource.password=test@123
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000

```

