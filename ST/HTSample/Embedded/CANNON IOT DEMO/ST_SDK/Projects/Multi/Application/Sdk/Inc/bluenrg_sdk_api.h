#ifndef _BLUENRG_SDK_API_H_
#define _BLUENRG_SDK_API_H_


#include "canon_hal.h"
#include "hal_types.h"
#include "bluenrg_gatt_server.h"
#include "bluenrg_gap.h"
#include "string.h"
#include "bluenrg_gap_aci.h"
#include "bluenrg_gatt_aci.h"
#include "hci_const.h"
#include "gp_timer.h"
#include "bluenrg_hal_aci.h"
#include "bluenrg_aci_const.h"   
#include "hci.h"
#include "hal.h"
#include "sm.h"
#include "debug.h"
#include <stdlib.h>

/*Ble State*/
#define BLE_CONNECTABLE		1
#define BLE_NOCONNECTABLE	0

/*adv parameter structure*/
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


/*Ble Connection State*/
extern volatile uint8_t Ble_conn_state;

void on_ready(void);
/*Init BLUENrg, HCI, Add Service*/
tBleStatus ble_init_bluenrg(void);
/*Set Adv Namee*/
tBleStatus ble_device_set_name(const char* new_device_name);
/*Add Adv Address*/
tBleStatus advertise_address(uint8_t* advaddress);
/*Config Adv Interval (min_adv_interval > 32*0.625)*/
void ble_device_set_advertising_interval(uint16_t interval);
/*Start to adv*/
tBleStatus ble_device_start_advertising(void);
/*set adv param*/
void ble_set_adv_param(char* adv_name, uint8_t*adv_address, uint8_t tx_power_level, uint16_t adv_interval);
/*Stop Adv*/
tBleStatus ble_device_stop_advertising(void);
/*Tx data(Notify)*/
tBleStatus ble_device_send(uint8_t type, uint32_t length, uint8_t* value);
/*To Disconnect Ble Connection*/
tBleStatus ble_user_disconnect_device(void);
/*Rx Data(write / write without responce)*/
void ble_device_on_message(uint8_t type, uint16_t length, uint8_t* value);
/*BLE On Connnection State*/
void ble_device_on_connect( void );
/*BLE On Disconnection State*/
void ble_device_on_disconnect(uint8_t reason);
/*BLE Set Tx Power*/
tBleStatus ble_device_set_tx_power(uint8_t level);


#endif //_BLUENRG_SDK_API_H_




