Read this in other languages: [English](README.md)  , [简体中文](README-CN.md)

[![CI](https://github.com/PangBaiWork/WeBlog/actions/workflows/main.yml/badge.svg?event=push)](https://github.com/PangBaiWork/WeBlog/actions/workflows/main.yml)
[![QQ](https://img.shields.io/badge/Join-QQ_Group-ff69b4)](https://qm.qq.com/q/1wJcBUfst2)
----
## Introduction 
What is a blog? It is another world for us,also a warm nest where you can say what you want to say,
share new and beautiful things you have learned. 
It is a pure and undisturbed land , where no one teaches you how to do things, and you can express everything wholeheartedly.
It can record your life, your growth. If you want, even your entire life.  

Static blog engines allow users to run their blogs at the lowest cost. 
Have you ever thought about writing and publishing your own blog on your phone?  
Perhaps it's a formal meeting, perhaps it's being in a noisy crowd, or perhaps it's a long and boring journey,
using the time wasted by meaningless things to summarize and share in your blog.
You may have already known some immature solutions, but now there are better ones.

## Description
**WeBlog** is a static blog editor that runs on Android. It provides the ability to write, build, 
and preview blogs. GIT, terminal, custom Shell scripts，Create articles, tag management, modern UI, provide a better blog writing environment.
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
## dependencies
[sora-editor](https://github.com/Rosemoe/sora-editor)  
[Termux](https://github.com/termux/termux-app)  
[Busybox](https://busybox.net/)   
...  
## Quick start
![img_1.png](doc/img_1.png)
### Start a new blog
#### Create an empty folder
The folder can be located in the SDCARD directory or in a private WeBlog directory.  
![img.png](doc/img.png)
#### Select a blog engine and create a blog
Currently available blog engines are `Hexo` and `Hugo`, creating a blog in Hexo may get stuck due to network problems, it is recommended to use Terminal to create a Hexo blog.   
If you are a newcomer to blogging, it is recommended to use Hugo to build your own blog, because the performance gap between Hexo and Hugo on Android is widened, specifically in building blogs and real-time previews   
(We found that Hexo on Android live preview performance is poor, compared to  Hugo preview（Its very fast) , Hexo face slow launching and slow rendering problems, if you use  markdownView preview ,it can be ignored).
#### live preview and script management
![img_2.png](doc/img_2.png)
Start live preview and fill in the preview port (default is 4000), after successful start the live preview button will change it's background, click the button again to close the live preview.    
We can access the live preview page via `http://localhost:4000/`, Hexo/Hugo will listen for markdown changes and render the page automatically.    
WeBlog provides Busybox to create a compact shell environment, including wget, vi, tar and other common commands.    
We can customize the build and deployment scripts through the scripts manager and create a `.sh` file in the `.scripts` folder of the project to add new scripts.    
Click the Run button in the upper right corner to execute one or more scripts (the order in which the scripts are executed is the order in which they appear in the list).  
![img_3.png](doc/img_3.png)
#### Creating a new article
Click the plus button on the home page to bring up the article creation page.  
![img_4.png](doc/img_4.png)
![img_5.png](doc/img_5.png)

When the title of the article is in ascii characters, WeBlog will automatically remove the spaces and capitalize the first letter of the string as the md file name.    
such as "Let us start" -> "LetUsStart.md".    
When the title of the article is non-ascii characters (i.e. non-English characters), WeBlog will automatically convert the characters to md5 code and then as md file name (this feature can be turned off in the settings, the title will become the file name).    
Each post can have one category, multiple tags, and a post description.    
Whenever you add a new category and tag, it will be recorded by the software, and can be used more conveniently next time you create an article.    
The article description will be displayed as a summary of the article in the article list of the website.  
#### for website deployment 
We recommend deploying your site using automated builds from Github, Vercel, etc. This allows you to modify and publish your articles (via git clone, push) on any platform.    
If you choose to push only the rendered files to the cloud, please also refer to the following command .  
##### Deploy via access token (recommended)   
Open github -> Setting -> Developer settings-> Personal access tokens-> Tokens(classic)-> Generate new token  
 
Create a token with enough permissions to access your Github page and access your Github repositories   
```shell
git -c http.extraheader="Authorization: Basic $(echo -n yourtoken: | base64)" push
````
or
```
git clone https://x-access-token:yourtoken@github.com/your-username/your-repo.git
```
Replace `yourtoken`, changing other information as appropriate.  
##### Deployment via ssh key
Open the terminal at the bottom of WeBlog and type this.  
```shell
git config --global user.name "yourname"
git config --global user.email "youremail"
ssh-keygen -t rsa -C your email.
```
Enter twice .    
After the terminal has finished outputting the message, run  
```shell 
cat /data/data/com.pangbai.weblog/files/home/.ssh/id_rsa.pub
``` 
Copy the output ssh-rsa****** to your clipboard  
Open the github page `https://github.com/settings/keys` and add the ssh key.    
then type  
```shell
ssh -T git@github.com
``` 
If the greeting words is output, it's successful, and you can access the repository via the git command now .  
##### Deployment via Vercel 
Sign up for a vercel account  
then run 
```shell
npm i vercel -g
``` 
Install vercel, if it fails, replace the npm image.    
After successful installation, run  
```shell
vc
``` 
Login to your vercel account using your email address(other choice can't work)   
The  deployment command is  
```shell 
vc --prod
```
