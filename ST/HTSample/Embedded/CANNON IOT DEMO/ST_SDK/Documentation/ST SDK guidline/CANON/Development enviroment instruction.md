##开发环境搭建
***
###Step1. 打开Keil工程文件(用git上提供的种子工程或者自己新建的keil工程)

![](http://i.imgur.com/JUor0aD.png)
***

###Step2. 进行工程配置
1.选择MCU型号：

![](http://i.imgur.com/ImwcMuY.png)
***

2.进行宏定义，头文件路径包含

![](http://i.imgur.com/7wuAPFb.png)
***

3.调试器选择：Jlink或者STLink

![](http://i.imgur.com/tw3piky.png)

![](http://i.imgur.com/BpKmQbA.png)
***

4.按如下图配置，出现图右侧的设备信息则表示设备正常连接，否则检查Jlink或者STLink连线。

![](http://i.imgur.com/ILYCi5I.png)
***

5.选择MCU Flash大小

![](http://i.imgur.com/ITuGbNW.png)
***

6.如果不适用种子工程，则需要添加相应的c文件

![](http://i.imgur.com/OZccvjL.png)

***

###Step3. 编译运行或者直接下载

![](http://i.imgur.com/NGZQOvA.png)

***
