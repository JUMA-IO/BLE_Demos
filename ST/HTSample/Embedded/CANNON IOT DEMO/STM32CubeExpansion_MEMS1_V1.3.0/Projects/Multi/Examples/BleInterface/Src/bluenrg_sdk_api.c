
#include "bluenrg_sdk_api.h"


#define BDADDR_SIZE 6
ble_gap_adv_params_t m_adv_params;
volatile uint8_t Ble_conn_state = BLE_CONNECTABLE;
static uint16_t connection_handle = 0 ,notification_enabled = 0;
uint16_t BLueNrgServHandle =0x0001, WriteCharHandle = 0x0006, WriteCmdCharHandle = 0x000A, ReadNotifyCharHandle = 0x000D, NotifyCharHandle = 0x0010;
uint16_t service_handle, dev_name_char_handle, appearance_char_handle;
const uint8_t AdvName[20];
char LocalName[20] = {AD_TYPE_COMPLETE_LOCAL_NAME,'B','l','u','e','N','R','G'};
static uint8_t AdvNameLen = 7;

__weak void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value){}
__weak void ble_device_on_connect( void ){}
__weak void ble_device_on_disconnect(uint8_t reason){}


/*Record Connection Handle*/
static void connection_information(uint16_t handle);
/*Read Req */
static void Read_Request_CB(uint16_t handle);
/*Add Service*/
static tBleStatus Add_Service(void);

/**
	*@brief  Board Initializition 
	*@param  None
	*@retval ret
	*/
tBleStatus ble_init_bluenrg(void)
{
#ifdef BLUENRG_MS  
  uint8_t SERVER_BDADDR[] = {0x04, 0x34, 0x00, 0xE1, 0x80, 0x02};
#else
  uint8_t SERVER_BDADDR[] = {0x12, 0x34, 0x00, 0xE1, 0x80, 0x02};
#endif
	uint8_t bdaddr[BDADDR_SIZE];
  
	tBleStatus ret;
  			/*gatt_Init*/
	ret = aci_gatt_init();
		
  if(ret){
		return BLE_GATT_INIT_FAILED;
  }
  
	
	#ifdef BLUENRG_MS
				/*BUG: Name Length*/
        ret = aci_gap_init(GAP_PERIPHERAL_ROLE, 0, AdvNameLen, &service_handle, &dev_name_char_handle, &appearance_char_handle);
	#else
        ret = aci_gap_init(GAP_PERIPHERAL_ROLE, &service_handle, &dev_name_char_handle, &appearance_char_handle);
	#endif
	
	if(ret != BLE_STATUS_SUCCESS){
    return BLE_GAP_INIT_FAILED;
  }
	
	ret = aci_gap_set_auth_requirement(MITM_PROTECTION_REQUIRED,
                                     OOB_AUTH_DATA_ABSENT,
                                     NULL,
                                     7,
                                     16,
                                     USE_FIXED_PIN_FOR_PAIRING,
                                     123456,
                                     BONDING);
  if (ret != BLE_STATUS_SUCCESS) {
		return BLE_STACK_INIT_FAILED;
  }
	
	/* add JUMA SERVICE*/
	ret = Add_Service();
	if(ret != BLE_STATUS_SUCCESS){
		return BLE_ADD_SERVICE_FAILED;
	}

}

/**
	*@brief Set Tx Power level	
	*@param Level
	*@retval ret
*/
tBleStatus ble_device_set_tx_power(uint8_t level)
{
	tBleStatus ret;
	/* Set output power level */
  ret = aci_hal_set_tx_power_level(1,level);
	
	return ret;
}
/**
	*@brief  Add Service 
	*@param  None
	*@retval ret
	*/
static tBleStatus Add_Service(void)
{
	tBleStatus ret;
	uint8_t service_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x00, 0x80, 0x00, 0x00};
	uint8_t command_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x01, 0x80, 0x00, 0x00};
	uint8_t event_char_uuid[16] = {  0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x02, 0x80, 0x00, 0x00};
	uint8_t bulkout_uuid[16] = { 0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x03, 0x80, 0x00, 0x00};
	uint8_t bulkin_uuid[16] = {  0x8C, 0xF9, 0x97,0xA6, 0xEE, 0x94, 0xE3,0xBC,0xF8, 0x21, 0xB2, 0x60, 0x04, 0x80, 0x00, 0x00};
	/*add service*/
	ret = aci_gatt_add_serv(UUID_TYPE_128,  service_uuid, PRIMARY_SERVICE, 11,
                          &BLueNrgServHandle);
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	/*Add command characteristic*/
	ret =  aci_gatt_add_char(BLueNrgServHandle, UUID_TYPE_128, command_uuid, 0x14,
                           CHAR_PROP_WRITE, ATTR_PERMISSION_NONE, GATT_NOTIFY_ATTRIBUTE_WRITE,
                           16, 1, &WriteCharHandle);
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	/*Add bulkout characteristic*/
	ret =  aci_gatt_add_char(BLueNrgServHandle, UUID_TYPE_128, bulkout_uuid, 0x14,
                           CHAR_PROP_WRITE_WITHOUT_RESP, ATTR_PERMISSION_NONE, 0,
                           16, 1, &WriteCmdCharHandle);//GATT_NOTIFY_ATTRIBUTE_WRITE
	if (ret != BLE_STATUS_SUCCESS) goto fail;
	
	/*Add event characteristic*/
	ret =  aci_gatt_add_char(BLueNrgServHandle, UUID_TYPE_128, event_char_uuid, 0x14,
                           CHAR_PROP_NOTIFY|CHAR_PROP_READ, ATTR_PERMISSION_NONE, 0,
                           16, 1, &ReadNotifyCharHandle);//GATT_NOTIFY_READ_REQ_AND_WAIT_FOR_APPL_RESP
	if (ret != BLE_STATUS_SUCCESS) goto fail;
						
	
	
	/*Add bulkin characteristic*/
	ret =  aci_gatt_add_char(BLueNrgServHandle, UUID_TYPE_128, bulkin_uuid, 0x14,
                           CHAR_PROP_NOTIFY, ATTR_PERMISSION_NONE, 0,
                           16, 1, &NotifyCharHandle);
	if (ret != BLE_STATUS_SUCCESS) goto fail;	
	
	return BLE_STATUS_SUCCESS; 
	
fail:
  return BLE_STATUS_ERROR ;  
}

/**
	*@brief  Set Adv Local Name
	*@param  None
	*@retval ret
	*/
tBleStatus ble_device_set_name(const char* new_device_name)
{
	 tBleStatus ret; 
		AdvNameLen = strlen(new_device_name);
		memcpy(AdvName,new_device_name,AdvNameLen);
		LocalName[0] = AD_TYPE_COMPLETE_LOCAL_NAME;
		memcpy(LocalName+1,new_device_name,AdvNameLen);
}

/**
	*@brief Adv Address
	*@param Adv Address
	*@retval ret
	*/
tBleStatus Advertise_Address(uint8_t* AdvAddress)
{
	tBleStatus ret;
  ret = aci_hal_write_config_data(CONFIG_DATA_PUBADDR_OFFSET,
                                  CONFIG_DATA_PUBADDR_LEN,
                                  AdvAddress);
  if(ret){
		return BLE_SET_BD_ADDR_FAILED;
  }
	
}

/**
	*@brief 	Start To Adv 
	*@param  	None
	*@retval 	ret
	*/
tBleStatus ble_device_start_advertising(void)
{
  tBleStatus ret;
	uint8_t uuid_length = 3;
	const uint8_t serviceUUIDList[] = {AD_TYPE_16_BIT_SERV_UUID,0xFE,0x90};
	
  /* disable scan response */
  hci_le_set_scan_resp_data(0,NULL);
	ret = aci_gatt_update_char_value(service_handle, dev_name_char_handle, 0,
                                   AdvNameLen, AdvName);
	
	/*min_adv_interval > 32*0.625*/
	ret = aci_gap_set_discoverable(ADV_IND, m_adv_params.interval, m_adv_params.interval, PUBLIC_ADDR, NO_WHITE_LIST_USE,
                                   AdvNameLen+1, LocalName, uuid_length, serviceUUIDList, 0, 0);//// start advertising
		
		
	if(ret){          
  while(1);
	}
	return ret;
 }

	

/**
	*@brief 	Start Adv 
	*@param  	None
	*@retval 	ret
	*/ 
tBleStatus ble_device_send(uint8_t type, uint32_t length, uint8_t* value)
{
	tBleStatus ret;
	uint8_t packet[20];
	if(notification_enabled == 0){
		
		return BLE_WAIT_REMOTE_ENABLE_NOTIFY;
	}
	
  if (length > 18) length = 18;
	packet[0] = type;
  packet[1] = length;
  memcpy(packet + 2, value, length);
	ret = aci_gatt_update_char_value(BLueNrgServHandle, ReadNotifyCharHandle, 0, packet[1]+2, packet);
	if (ret != BLE_STATUS_SUCCESS){
   
    return BLE_STATUS_ERROR ;
  }
}
/**
	*@brief 	Stop Adv 
	*@param  	None
	*@retval 	ret
	*/
tBleStatus ble_device_stop_advertising(void)
{
	tBleStatus ret;
	ret = aci_gap_set_non_discoverable();
	
	return ret;
}
/**
	*@brief 	Config Adv Interval (min_adv_interval > 32*0.625) 
	*@param  	None
	*@retval 	ret
	*/
void ble_device_set_advertising_interval(uint16_t interval)
{
	/*min_adv_interval > 32*0.625*/
	m_adv_params.interval = interval;
}
/**
	*@brief 	To disconnect Ble Connection 
	*@param  	None
	*@retval 	ret
	*/
tBleStatus ble_user_disconnect_device(void)
{
	tBleStatus ret;
	ret = aci_gap_terminate(connection_handle, HCI_OE_USER_ENDED_CONNECTION);
	Ble_conn_state = BLE_CONNECTABLE;
	return ret;
}
/**
	*@brief 	Record Connection Handle
	*@param  	None
	*@retval 	None
	*/
static void connection_information(uint16_t handle)
{  
  connection_handle = handle;

}
/**
	*@brief 	Read Req
	*@param  	None
	*@retval 	None
	*/
static void Read_Request_CB(uint16_t handle)
{  
  if(handle == BLueNrgServHandle + 1){
    //Acc_Update((AxesRaw_t*)&axes_data);
  }
 
  if(connection_handle != 0){
    aci_gatt_allow_read(connection_handle);
	}		
}

/**
 * @brief  Callback processing the ACI events.
 * @note   Inside this function each event must be identified and correctly
 *         parsed.
 * @param  void* Pointer to the ACI packet
 * @retval None
 */
void HCI_Event_CB(void *pckt)
{
  hci_uart_pckt *hci_pckt = pckt;
  /* obtain event packet */
  hci_event_pckt *event_pckt = (hci_event_pckt*)hci_pckt->data;
  if(hci_pckt->type != HCI_EVENT_PKT)
    return;
  
  switch(event_pckt->evt){
    
  case EVT_DISCONN_COMPLETE:
    {
			notification_enabled = 0;
			ble_device_on_disconnect(event_pckt->data[3]);
    }
    break;
    
  case EVT_LE_META_EVENT:
    {
      evt_le_meta_event *evt = (void *)event_pckt->data;
      
      switch(evt->subevent){
      case EVT_LE_CONN_COMPLETE:
        {
					ble_device_on_connect();
          evt_le_connection_complete *cc = (void *)evt->data;
					connection_information(cc->handle);
					
        }
        break;
      }
    }
    break;
    
  case EVT_VENDOR:
    {
      evt_blue_aci *blue_evt = (void*)event_pckt->data;
      switch(blue_evt->ecode){

			case EVT_BLUE_GATT_ATTRIBUTE_MODIFIED:         
        {
          /* this callback is invoked when a GATT attribute is modified
          extract callback data and pass to suitable handler function */
          evt_gatt_attr_modified *evt = (evt_gatt_attr_modified*)blue_evt->data;
					///on message
					if(evt->att_data[1] > 0){
						ble_device_on_message(evt->att_data[0], evt->att_data[1], (evt->att_data)+2);	
					}else if(evt->att_data[0] == 1){
						notification_enabled = 1;
					}						
        }
        break; 

      case EVT_BLUE_GATT_READ_PERMIT_REQ:
        {
          evt_gatt_read_permit_req *pr = (void*)blue_evt->data;                    
          Read_Request_CB(pr->attr_handle);                    
        }
        break;
				
      }
    }
    break;
  }    
}
