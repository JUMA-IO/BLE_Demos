mesh中的demo是一个灯控程序，即可以通过手机给任意一个设备发送指令，从而控制所有设备。

可以在STM32_Platform\application\juma目录下找到嵌入式程序，双击CAF.uvprojx即可运行程序，可以通过apk文件夹里的apk来测试程序。

调试助手的F1~F9对应不同的Type0x00~0x08。

mesh协议demo需要配置:
1.设备的MESH_ID(app.c的宏定义)

2.广播名字用于区分不同设备(board_name，app.c中)

3.demo用的四个设备的MESH_ID分别定义为0x0001,0x0002,0x0004,0x0008.
名字也需改成不同的以方便识别。

4.移动端的测试软件名字为light demo。在目录android中