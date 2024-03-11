Read this in other languages: [English](README.md)  , [简体中文](README-CN.md),

[![CI](https://github.com/Rosemoe/CodeEditor/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/PangBaiWork/WeBlog/actions/workflows/main.yml)
[![QQ](https://img.shields.io/badge/Join-QQ_Group-ff69b4)](https://qm.qq.com/q/1wJcBUfst2)
----
## 引言 
博客是什么？它是我们的另一片天地，在这里你可以说你想说的话，分享自己学到的新东西，美好的事，是温馨的小窝。
它是不被世俗打扰的净土，没有人在这里教你做事，你可以倾心陈述一切。
它是可以记录你的生活，你的成长，如果你想，甚至是你的一生。
静态博客引擎允许使用者们以最低成本运行自己的博客，热爱生活的你，有没有想过在手机上编写发布自己的博客？
也许是一场形式上的会议，也许是置身于喧闹的人群，也许是很长很长的无聊旅途，把那些被毫无意义之事浪费的时间用在总结与分享之上。
你或许已经了解过一些不成熟的方案，但现在这里提供了更好的方案。
## 描述
**WeBlog**是一个运行在安卓的静态博客编写器，他提供从编写,构建到预览预览博客的能力，同时包含GIT,终端,自定义Shell脚本等功能。
新建文章，标签管理，现代化的UI，可以提供更好博客编写的体验。
### TODO
* [x] Editor and Highlight
* [x] File Manager(with post title parsing)
* [x] NPM and GIT support
* [x] Fixed node_modules in External storage
* [x] Create-Article panel
* [x] Tags and categories select
* [x] Hexo project support
* [x] Hugo project support
* [x] Preference support
* [x] Environment information 
## 鸣谢的开源项目
[sora-editor](https://github.com/Rosemoe/sora-editor)  
[Termux](https://github.com/termux/termux-app)  
[Busybox](https://busybox.net/)   
...  
## 快速开始
![img_1.png](doc/img_1.png)
### 开始新的博客
#### 创建一个空文件夹
文件夹可以位于SDCARD目录或者WeBlog私有目录。
![img.png](doc/img.png)
#### 选择一个博客引擎并创建博客
当前可选博客引擎有`Hexo`和`Hugo`，在Hexo创建博客时可能会因为网络问题卡住，推荐使用终端创建Hexo博客。  
如果你是一个博客新人，推荐使用Hugo搭建自己的博客，因为在安卓上Hexo与Hugo性能差距被拉大，具体表现在构建博客和实时预览 
(我们发现Hexo在安卓上实时预览表现不佳，相对于Hugo预览的秒载，Hexo面临启动慢和渲染慢的问题，如果你只使用软件自带的markdownView预览大可忽略这些差距)。
#### 实时预览和脚本管理
![img_2.png](doc/img_2.png) 
启动实时预览并填入预览端口(默认为4000)，启动成功后实时预览按钮将会改变背景，再次点击按钮可关闭实时预览。  
我们可以通过`http://localhost:4000/` 访问实时预览的网页，此时Hexo会监听markdown修改并自动渲染网站页面。  
WeBlog提供Busybox创造一个精简的shell环境，包含wget,vi,tar等常用命令。  
我们可以通过脚本管理自定义构建和部署的脚本，在项目的`.scripts`文件夹自建`.sh` 文件即可添加新的脚本  
单击右上角运行按钮即可执行一个或多个脚本(脚本执行的先后顺序为脚本在列表里排列的顺序)
![img_3.png](doc/img_3.png)
#### 创建新文章
单击主页的加号按钮以弹出文章创建页面
![img_4.png](doc/img_4.png)
![img_5.png](doc/img_5.png) 
当文章标题为ascii字符时,WeBlog会自动以去除空格，并首字母大写的后的字符串作为md文件名  
如"Let us start" -> "LetUsStart.md" 
当文章标题为非ascii字符时(即非英文字符)，WeBlog会自动把字符转化为md5码再作为md文件名(此功能可在设置里关闭，关闭后以标题为文件名) 
每篇文章可以有一个分类，多个标签，和一个文章描述。  
每次添加新的分类和标签都会被软件记录下来，下次创建文章能够更方便地使用。  
文章描述将作为文章摘要在网站的文章列表显示。
#### 进行网站部署 
我们推荐使用Github,Vercel等网站的自动构建进行网站部署，这样你可以在任何平台对你的文章进行修改和发布(通过git clone,push)，  
若是你选择仅推送渲染文件到托管网站，请也参考一下命令进行操作
##### 通过访问令牌部署(推荐) 
打开github -> Setting -> Developer settings-> Personal access tokens-> Tokens(classic)-> Generate new token
 
创建一个权限足以访问你Github page的令牌，并通过以下形式访问你的Github仓库
```shell
git -c http.extraheader="Authorization: Basic $(echo -n 你的令牌: | base64)" push
```
或
```
git clone https://x-access-token:你的令牌@github.com/your-username/your-repo.git
```
将`你的令牌`替换为刚得到的令牌,适当修改其他信息  
更多用法可以查找关于令牌的资料。
##### 通过ssh密钥部署 
打开WeBlog底部终端,按你的Github实际信息进行输入.
```shell
git config --global user.name "yourname"
git config --global user.email "youremail"
ssh-keygen -t rsa -C 你的邮箱
``` 
连续回车两次 
终端输出完信息后，再执行
```shell 
cat /data/data/com.pangbai.weblog/files/home/.ssh/id_rsa.pub
``` 
复制输出的信息ssh-rsa******到剪切板  
打开github页面`https://github.com/settings/keys` 添加ssh密钥  
添加完毕后输入
```shell
ssh -T git@github.com
```
如果输出问候语，即成功，此后可通过git命令访问仓库
##### 通过Vercel部署
使用之前请注册vercel账号
```shell
npm i vercel -g
``` 
安装vercel,如果出现失败请更换npm镜像或科学上网。  
安装成功后执行
```shell
vc
``` 
使用邮箱登录你的vercel账号  
登录完成后根据喜好配置vercel即可  
正式部署命令
```shell
vc --prod
```
