# gmall
谷粒商品商城项目

## 1.拉取模板

配置好github密钥

初始化仓库

```
git init
```

拉取模板

```
git clone git@github.com:hh2134/gmall.git
```

## 2.提交代码

```
添加到暂存区
git add	gamll/
提交到本地仓库
git commit -m "测试仓库的拉取和提交" gamll/
提交到远程仓库
git push git@github.com:hh2134/gmall.git master
```

## 3.基础工程copy

将基础工程的 **gmall-admin** 和 **gmall-common** 和 **pom.xml** 复制到 **gmall** 项目中

## 4.修改pom.xml

修改 **gmall-common** 的 **pom.xml**

```xml
<artifactId>gmall-0623</artifactId>
<groupId>com.atguigu</groupId>
<version>0.0.1-SNAPSHOT</version>
```

改成

```xml
<groupId>com.atguigu</groupId>
<artifactId>gmall</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

## 5.添加忽略文件

修改 **.gitignore**

添加

```
.idea/
target/
*.iml
```

