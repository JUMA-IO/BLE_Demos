//#include "sensor_service.h"
#include "bluenrg_sdk_api.h"
static uint8_t echoBuffer[20];

void User_Process(void)
{
  if(Ble_conn_state){
		const char *name = "BlueNRG";
		ble_device_set_name(name);
		ble_device_config_advertising_interval(100);//min_adv_interval > 32*0.625
		ble_device_start_advertising();
    Ble_conn_state = BLE_NOCONNECTABLE;
  }
	
}

/* Device On Message */
void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{  	
			
      //BSP_LED_Toggle(LED2);
			if((*value) & 0x01){
				BSP_LED_On(LED0);
			}else{
				BSP_LED_Off(LED0);
			}

			ble_device_send(type, length, value);
			
}


/* Device on connect */
void ble_device_on_connect(void)
{
		BSP_LED_Toggle(LED0);
}

/* Device on disconnect */
void ble_device_on_disconnect(uint8_t reason)
{
	  BSP_LED_Toggle(LED0);
		/* Make the device connectable again. */
		Ble_conn_state = BLE_CONNECTABLE;
}



