# 这是一个基于springboot的blog开源博客系统， 数据库采用mysql， 模板系统采用thymeleaf， 连接数据库接口采用springboot-data-jpa

## 1、下载代码

```shell
git clone https://github.com/helunhelun/blog-springboot.git
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

## 3、运行工程

<p style="font-size:20px;">在application.properties文件中修改监听的端口</p>

```yaml
server.port=8000
server.tomcat.max-connections=8192
server.tomcat.threads.max=200
```

<p style="font-size:20px;">我们使用idea中打开工程，运行maven重加载，下载相关的jar库</p>

<p style="font-size:20px;">点击idea中右上角的运行按钮，运行即可</p>

## 4、创建管理员账户

<p style="font-size:20px;">工程运行的时候，需要为网站创建管理员账户， 访问网址： http://localhost:8000/launchpad</p>


<p style="font-size:20px;">填写相关的用户信息（邮箱， 用户名，昵称， 密码等等）， 成功之后访问http://localhost:8000/login 网址登陆， 在博客后台创建相关的文章分类与标签， 最少写四篇文章，然后提交</p>

## 5、我的网站

<p style="font-size:20px;">具体运行效果可以查看我的网站: <a href="https://www.ivdone.cn">https://www.ivdone.cn</a></p>

<p style="font-size:20px;">部分运行界面</p>

![image](https://github.com/helunhelun/blog_sprinigboot/blob/master/20201224134100.png)

![image](https://github.com/helunhelun/blog_sprinigboot/blob/master/20201224134204.png)

![image](https://github.com/helunhelun/blog_sprinigboot/blob/master/20201224134237.png)








