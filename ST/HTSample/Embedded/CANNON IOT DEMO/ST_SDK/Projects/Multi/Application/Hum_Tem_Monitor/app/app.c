//#include "sensor_service.h"

#include "app.h"
/*start adv*/

const char *name = "BlueNRG_IOT";
uint8_t adv_address[] = {0x08, 0x05, 0x04, 0x03, 0x02, 0x04};	
uint8_t tx_power_level = 7;
uint16_t adv_interval = 100;
static float humidity;
static float temperature;

void on_ready(void)
{
	hum_temp_monitor_init();
	/*Config Adv Parameter And Ready to Adv*/
	ble_set_adv_param(name, adv_address, tx_power_level, adv_interval);
	ble_device_start_advertising();
}

void user_process(void)
{
	/*Collection hum,temp data*/
	read_temp_hum();
}

/*init hts221*/
void hum_temp_monitor_init(void)
{
	/* Initialize the HUM temp */
		while(BSP_HUM_TEMP_isInitialized() != 1){
			HAL_Delay(10);
			BSP_HUM_TEMP_Init();
		}

}

/*Accept humidity,tempreture data*/
void read_temp_hum(void)
{
	uint8_t temp[2] = {0};
	uint16_t temp_1 = 0;
//	/*humity*/
	BSP_HUM_TEMP_GetHumidity(&humidity);
	temp_1 = (uint16_t)(humidity*100);
	temp[0] = temp_1 >> 8;
	temp[1] = temp_1 & 0xFF;
	ble_device_send(0x01, 2, temp);
	/*Temperature*/
	BSP_HUM_TEMP_GetTemperature(&temperature);
	temp_1 = (uint16_t)(temperature*100);
	temp[0] = temp_1 >> 8;
	temp[1] = temp_1 & 0xFF;
	ble_device_send(0x00, 2, temp);
	HAL_Delay(1000);
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
	
}
/* Device on disconnect */
void ble_device_on_disconnect(uint8_t reason)
{
	/* Make the device connectable again. */
	Ble_conn_state = BLE_CONNECTABLE;
}
