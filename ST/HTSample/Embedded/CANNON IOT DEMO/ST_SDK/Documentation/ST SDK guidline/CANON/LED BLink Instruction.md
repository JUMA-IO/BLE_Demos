##CANON LED Blink Demo
***
###Step1. Open the Keil project：BlueNRG_SDK.uvproj

![](http://i.imgur.com/Vuox5bl.png)
***

###Step2.Click the build button to create a Hex file

![](http://i.imgur.com/yiwI1p2.png)
***

###Step3.CLick the load button to load the Hex into Flash Rom

![](http://i.imgur.com/yatJwos.png)
***

###Step4.

***
#3.使用安卓调试助手连接CANON板子。
安装好安卓调试助手后，
![](http://i.imgur.com/ndCv2Ee.png)

点击Scan进行设备扫描。之后会看到如下的扫描结果：

![](http://i.imgur.com/OIezg9Y.png)

点击BlueNRG_LED,再点击CONNECT按钮进行连接。

之后可以通过 Android发送相应的数据（数据格式：第一个字节为类型，第二个字节为接下来的数据的数据长度，第三个字节为数据。目前类型这一字节暂时无任何意义可随意填写，例如：0x010101，代表类型：0x01，长度：0x01，数据：0x01），点亮LED时数据位为：0x01，应发送（0x010101）熄灭时数据位为：0x00，应发送（0x010100）。如果工作正常的话，LED会正常工作。
***

