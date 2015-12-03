##蓝牙数据传输ECHO例程说明
####Step1 Init：
***
***
1.BNRG_SPI_Init();
说明：因为MCU与BlueNRG之间通过SPI进行通信，因此，需要先初始化SPI。
***
2.HCI_Init();
说明：蓝牙的Host层与Controller通过HCI进行数据交互，此步骤初始化HCI层的数据队列。
***
3.BlueNRG_RST();
说明：通过SPI Reset BlueNRG芯片
***
4.ble_start_adv(name, AdvAddress, TxPowerLevel, AdvInterval);
说明：调用
Advertise_Address(AdvAddress);
ble_device_set_name(AdvName);
ble_init_bluenrg();
ble_device_set_tx_power(TxPowerLevel);
ble_device_set_advertising_interval(AdvInterval);
配置广播参数（广播名字，广播地址，发射功率，广播间隔）准备广播；
***
***
####Step2 HCI Process ： 
***
1.HCI_Process()；
说明：处理MCU与BlueNRG之间的数据交互，将从BlueNRG接收到的数据传递给上层。
***
####Step3 Echo Demo：
***
***
1.User_Process()；
说明：调用蓝牙蓝牙广播接口
ble_device_start_advertising();开始蓝牙广播，
将设备状态设置为不可连接状态(默认状态为可连接状态)；
***
2.void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)；
说明：蓝牙收到数据的回调函数。
echo demo在这里调用数据发送接口ble_device_send(type, length, value);
将收到的数据发送回去。
***
3.void ble_device_on_connect(void)；
说明：蓝牙设备建立连接的回调函数,Echo这里点亮LED0。
***
4.void ble_device_on_disconnect(uint8_t reason)
说明：蓝牙断开连接的回调函数，传入断开连接的状态，Enable 蓝牙可连接状态。
***
***

