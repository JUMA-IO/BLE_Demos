#ifndef _JUMA_TEST_API_H_
#define _JUMA_TEST_API_H_

#include "stm32_bluenrg_ble.h"
#include "osal.h"
#include "sensor_service.h"


typedef struct
{
  uint8_t               type;                 /**< See @ref BLE_GAP_ADV_TYPES. */
  //ble_gap_addr_t       *p_peer_addr;          /**< For @ref BLE_GAP_ADV_TYPE_ADV_DIRECT_IND mode only, known peer address. */
  uint8_t               fp;                   /**< Filter Policy, see @ref BLE_GAP_ADV_FILTER_POLICIES. */
  //ble_gap_whitelist_t  *p_whitelist;          /**< Pointer to whitelist, NULL if none is given. */
  uint16_t              interval;             /**< Advertising interval between 0x0020 and 0x4000 in 0.625 ms units (20ms to 10.24s), see @ref BLE_GAP_ADV_INTERVALS.
                                                   - If type equals @ref BLE_GAP_ADV_TYPE_ADV_DIRECT_IND, this parameter must be set to 0 for high duty cycle directed advertising.
                                                   - If type equals @ref BLE_GAP_ADV_TYPE_ADV_DIRECT_IND, set @ref BLE_GAP_ADV_INTERVAL_MIN <= interval <= @ref BLE_GAP_ADV_INTERVAL_MAX for low duty cycle advertising.*/
  uint16_t              timeout;              /**< Advertising timeout between 0x0001 and 0x3FFF in seconds, 0x0000 disables timeout. See also @ref BLE_GAP_ADV_TIMEOUT_VALUES. If type equals @ref BLE_GAP_ADV_TYPE_ADV_DIRECT_IND, this parameter must be set to 0 for High duty cycle directed advertising. */
 // ble_gap_adv_ch_mask_t channel_mask;         /**< Advertising channel mask. @see ble_gap_channel_mask_t for documentation. */
} ble_gap_adv_params_t;

extern uint16_t testServHandle, testCharHandle, testCharHandle_2, testCharHandle_3, testCharHandle_4;
extern uint16_t service_handle, dev_name_char_handle, appearance_char_handle;
extern  volatile uint16_t connection_handle;
extern volatile uint8_t set_connectable;
extern volatile int connected;
extern volatile uint8_t notification_enabled;

void ble_init_board(void);
tBleStatus Add_Juma_Service(void);
void ble_device_send(uint8_t type, uint32_t length, uint8_t* value);
void ble_device_set_name(const char* new_device_name);
void ble_device_start_advertising(void);
void ble_device_stop_advertising(void);
void ble_device_set_advertising_interval(uint16_t interval);
void ble_device_disconnect( void );
void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value);
void ble_device_on_connect(void);
void ble_device_on_disconnect(uint8_t reason);
/* connection handle*/
void connection_information(uint16_t handle);
/*enable notify*/
void Read_Request_CB(uint16_t handle);

#endif //_JUMA_TEST_API_H_




