
#include "juma_test_api.h"


#define BDADDR_SIZE 6
ble_gap_adv_params_t m_adv_params;
/* board initializition */
void ble_init_board(void)
{
#ifdef BLUENRG_MS  
  uint8_t SERVER_BDADDR[] = {0x12, 0x34, 0x00, 0xE1, 0x80, 0x02};
#else
  uint8_t SERVER_BDADDR[] = {0x12, 0x34, 0x00, 0xE1, 0x80, 0x03};
#endif
	uint8_t bdaddr[BDADDR_SIZE];
  
	int ret;
  
  /* Initialize the BlueNRG SPI driver */
  BNRG_SPI_Init();
  
  /* Initialize the BlueNRG HCI */
  HCI_Init();
  
  /* Reset BlueNRG hardware */
  BlueNRG_RST();
  
  /* The Nucleo board must be configured as SERVER */
  Osal_MemCpy(bdaddr, SERVER_BDADDR, sizeof(SERVER_BDADDR));
  
  ret = aci_hal_write_config_data(CONFIG_DATA_PUBADDR_OFFSET,
                                  CONFIG_DATA_PUBADDR_LEN,
                                  bdaddr);
  if(ret){
    PRINTF("Setting BD_ADDR failed.\n");
  }
  
  ret = aci_gatt_init();    
  if(ret){
    PRINTF("GATT_Init failed.\n");
  }
	
	#ifdef BLUENRG_MS
        ret = aci_gap_init(GAP_PERIPHERAL_ROLE, 0, 0x07, &service_handle, &dev_name_char_handle, &appearance_char_handle);
	#else
        ret = aci_gap_init(GAP_PERIPHERAL_ROLE, &service_handle, &dev_name_char_handle, &appearance_char_handle);
	#endif
	
	if(ret != BLE_STATUS_SUCCESS){
    PRINTF("GAP_Init failed.\n");
  }
	
	ret = aci_gap_set_auth_requirement(MITM_PROTECTION_REQUIRED,
                                     OOB_AUTH_DATA_ABSENT,
                                     NULL,
                                     7,
                                     16,
                                     USE_FIXED_PIN_FOR_PAIRING,
                                     123456,
                                     BONDING);
  if (ret == BLE_STATUS_SUCCESS) {
    PRINTF("BLE Stack Initialized.\n");
  }
	/* Set output power level */
  ret = aci_hal_set_tx_power_level(1,4);
	/* add JUMA SERVICE*/
	ret = Add_Juma_Service();
	if(ret == BLE_STATUS_SUCCESS)
  PRINTF("JUMA service added successfully.\n");
  else
  PRINTF("Error while adding JUMA service.\n");  
}

/* Add Service */
tBleStatus Add_Juma_Service(void)
{
	tBleStatus ret;
	uint8_t service_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x00, 0x80, 0x00, 0x00};
	uint8_t command_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x01, 0x80, 0x00, 0x00};
	uint8_t event_char_uuid[16] = {  0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x02, 0x80, 0x00, 0x00};
	uint8_t bulkout_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x03, 0x80, 0x00, 0x00};
	uint8_t bulkin_uuid[16] = {  0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x04, 0x80, 0x00, 0x00};
	////add service
	ret = aci_gatt_add_serv(UUID_TYPE_128,  service_uuid, PRIMARY_SERVICE, 11,
                          &testServHandle);//&p_data->service_handle
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	////Add command characteristic
	ret =  aci_gatt_add_char(testServHandle/*p_data->service_handle*/, UUID_TYPE_128, command_uuid, 6,
                           CHAR_PROP_WRITE, ATTR_PERMISSION_NONE, GATT_NOTIFY_ATTRIBUTE_WRITE,
                           16, 1, &testCharHandle);//&p_data->command_handles // | CHAR_PROP_WRITE//GATT_NOTIFY_ATTRIBUTE_WRITE
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	////Add bulkout characteristic
	ret =  aci_gatt_add_char(testServHandle/*p_data->service_handle*/, UUID_TYPE_128, bulkout_uuid, 6,
                           CHAR_PROP_WRITE_WITHOUT_RESP, ATTR_PERMISSION_NONE, 0,
                           16, 1, &testCharHandle_2);//&p_data->bulkout_handles //CHAR_PROP_WRITE_WITHOUT_RESP
	if (ret != BLE_STATUS_SUCCESS) goto fail;
	
	////Add event characteristic
	ret =  aci_gatt_add_char(testServHandle/*p_data->service_handle*/, UUID_TYPE_128, event_char_uuid, 20,
                           CHAR_PROP_NOTIFY|CHAR_PROP_READ, ATTR_PERMISSION_NONE, GATT_NOTIFY_READ_REQ_AND_WAIT_FOR_APPL_RESP,
                           16, 1, &testCharHandle_3);//&p_data->event_handles
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	
	
	////Add bulkin characteristic
	ret =  aci_gatt_add_char(testServHandle/*p_data->service_handle*/, UUID_TYPE_128, bulkin_uuid, 20,
                           CHAR_PROP_NOTIFY, ATTR_PERMISSION_NONE, 0,
                           16, 1, &testCharHandle_4);//&p_data->bulkin_handles
	if (ret != BLE_STATUS_SUCCESS) goto fail;	
	
	return BLE_STATUS_SUCCESS; 
	
fail:
  PRINTF("Error while adding ACC service.\n");
  return BLE_STATUS_ERROR ;  
}

/* Set Advtiser's Name */
////set adv name
void ble_device_set_name(const char* new_device_name)
{
	 int ret;  
	 ret = aci_gatt_update_char_value(service_handle, dev_name_char_handle, 0,
                                   strlen(new_device_name), (uint8_t *)new_device_name);
	  if(ret){
    PRINTF("aci_gatt_update_char_value failed.\n");            
    while(1);
		}
}

/* start advertising */
void ble_device_start_advertising(void)
{
    tBleStatus ret;
    const char local_name[] = {AD_TYPE_COMPLETE_LOCAL_NAME,'J','U','M','A','&','S','T'};////set advertising name
    
    /* disable scan response */
    hci_le_set_scan_resp_data(0,NULL);
    
    PRINTF("General Discoverable Mode ");
    /*
    Advertising_Event_Type, Adv_Interval_Min, Adv_Interval_Max, Address_Type, Adv_Filter_Policy,
    Local_Name_Length, Local_Name, Service_Uuid_Length, Service_Uuid_List, Slave_Conn_Interval_Min,
    Slave_Conn_Interval_Max
    */
		//min_adv_interval > 32*0.625
		 ret = aci_gap_set_discoverable(ADV_IND, m_adv_params.interval, m_adv_params.interval, PUBLIC_ADDR, NO_WHITE_LIST_USE,
                                   sizeof(local_name), local_name, 0, NULL, 0, 0);//// start advertising
    
		PRINTF("%d\n",ret);
  }

/* Send Data */  
void ble_device_send(uint8_t type, uint32_t length, uint8_t* value)
{
	tBleStatus ret;
	uint8_t packet[20];

  if (length > 18) length = 18;
	packet[0] = type;
  packet[1] = length;
  memcpy(packet + 2, value, length);
	ret = aci_gatt_update_char_value(testServHandle, testCharHandle_3, 0, packet[1]+2, packet);
	if (ret != BLE_STATUS_SUCCESS){
    PRINTF("Error while updating ACC characteristic.\n") ;
   // return BLE_STATUS_ERROR ;
  }
}

/* Stop Advertising */
void ble_device_stop_advertising(void)
{
	tBleStatus ret;
	ret = aci_gap_set_non_discoverable();
	PRINTF("%d\n",ret);
}

/* Set Advertising interval */
void ble_device_set_advertising_interval(uint16_t interval)
{
	//min_adv_interval > 32*0.625
	m_adv_params.interval = interval;
}

/* Device On Message */
void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value)
{  
      //BSP_LED_Toggle(LED2);
			if((*value) & 0x01){
				BSP_LED_On(LED2);
			}else{
				BSP_LED_Off(LED2);
			}
			
}

void ble_device_disconnect( void )
{
	aci_gap_terminate(connection_handle, HCI_OE_USER_ENDED_CONNECTION);
	set_connectable = TRUE;
}


/* Device on connect */
void ble_device_on_connect(void)
{
		connected = TRUE;
		BSP_LED_Toggle(LED2);
}

/* Device on disconnect */
void ble_device_on_disconnect(uint8_t reason)
{
	  BSP_LED_Toggle(LED2);
		/* Make the device connectable again. */
		set_connectable = TRUE;
		notification_enabled = FALSE;
}

void connection_information(uint16_t handle)
{  
  connected = TRUE;
  connection_handle = handle;

}

