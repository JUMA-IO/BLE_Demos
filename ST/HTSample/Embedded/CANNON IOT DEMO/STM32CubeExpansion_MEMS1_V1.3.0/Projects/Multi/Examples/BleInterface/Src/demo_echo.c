//#include "sensor_service.h"
#include "demo.h"
#include "main.h"
/*start adv*/
extern volatile uint8_t test_status;
uint8_t test_Buffer[18] = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x10,0x11,0x12,};
void User_Process(void)
{
  if(Ble_conn_state){
		ble_device_start_advertising();
    Ble_conn_state = BLE_NOCONNECTABLE;
  }
	Read_Temp_Hum();
}


/*send message*/
void user_send_message(uint8_t type ,uint8_t* Data)
{
	ble_device_send(type, 2, Data);
}

/* Device On Message */
void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{  	
	if(type == 0x00){
		if(*value == 0x00){
			BSP_LED_On(LED0);
		}
		if(*value == 0x01){
			BSP_LED_Off(LED0);
			
		}
	}
}
/* Device on connect */
void ble_device_on_connect(void)
{
		tBleStatus ret = BLE_WAIT_REMOTE_ENABLE_NOTIFY;
		
	
	//BSP_LED_Toggle(LED0);
}
/* Device on disconnect */
void ble_device_on_disconnect(uint8_t reason)
{
	/* Make the device connectable again. */
	Ble_conn_state = BLE_CONNECTABLE;
}


