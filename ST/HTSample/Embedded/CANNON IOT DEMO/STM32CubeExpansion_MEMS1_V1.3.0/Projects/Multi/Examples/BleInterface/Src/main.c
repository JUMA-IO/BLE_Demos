/**
  ******************************************************************************
  * @file    main.c 
  * @author  CL
  * @version V1.0.0
  * @date    04-July-2014
  * @brief   This application contains an example which shows how implementing
  *          a proprietary Bluetooth Low Energy profile: the sensor profile.
  *          The communication is done using a Nucleo board and a Smartphone
  *          with BTLE.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2014 STMicroelectronics</center></h2>
  *
  * Redistribution and use in source and binary forms, with or without modification,
  * are permitted provided that the following conditions are met:
  *   1. Redistributions of source code must retain the above copyright notice,
  *      this list of conditions and the following disclaimer.
  *   2. Redistributions in binary form must reproduce the above copyright notice,
  *      this list of conditions and the following disclaimer in the documentation
  *      and/or other materials provided with the distribution.
  *   3. Neither the name of STMicroelectronics nor the names of its contributors
  *      may be used to endorse or promote products derived from this software
  *      without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  *
  ******************************************************************************
  */

/* Includes ------------------------------------------------------------------*/
#include "canon_hal.h"

#include "osal.h"
//#include "sensor_service.h"
#include "debug.h"
#include "stm32_bluenrg_ble.h"
#include "bluenrg_sdk_api.h"

/** @addtogroup X-CUBE-BLE1_Applications
 *  @{
 */

/** @defgroup SensorDemo
 *  @{
 */

/** @defgroup MAIN 
 * @{
 */

/** @defgroup MAIN_Private_Defines 
 * @{
 */
/* Private defines -----------------------------------------------------------*/

/**
 * @}
 */
 
/* Private macros ------------------------------------------------------------*/

/** @defgroup MAIN_Private_Variables
 * @{
 */
/* Private variables ---------------------------------------------------------*/

/**
 * @}
 */

/** @defgroup MAIN_Private_Function_Prototypes
 * @{
 */
/* Private function prototypes -----------------------------------------------*/
void User_Process(void);
/**
 * @}
 */

/**
 * @brief  Main function to show how to use the BlueNRG Bluetooth Low Energy
 *         expansion board to send data from a Nucleo board to a smartphone
 *         with the support BLE and the "BlueNRG" app freely available on both
 *         GooglePlay and iTunes.
 *         The URL to the iTunes for the "BlueNRG" app is
 *         http://itunes.apple.com/app/bluenrg/id705873549?uo=5
 *         The URL to the GooglePlay is
 *         https://play.google.com/store/apps/details?id=com.st.bluenrg
 *         The source code of the "BlueNRG" app, both for iOS and Android, is
 *         freely downloadable from the developer website at
 *         http://software.g-maps.it/
 *         The board will act as Server-Peripheral.
 *
 *         After connection has been established:
 *          - by pressing the USER button on the board, the cube showed by
 *            the app on the smartphone will rotate.
 *          
 *         The communication is done using a vendor specific profile.
 *
 * @param  None
 * @retval None
 */

static void ble_start_adv(char* AdvName, uint8_t*AdvAddress, uint8_t TxPowerLevel, uint16_t AdvInterval);

int main(void)
{
	/* STM32Cube HAL library initialization:
   *  - Configure the Flash prefetch, Flash preread and Buffer caches
   *  - Systick timer is configured by default as source of time base, but user 
   *    can eventually implement his proper time base source (a general purpose 
   *    timer for example or other time source), keeping in mind that Time base 
   *    duration should be kept 1ms since PPP_TIMEOUT_VALUEs are defined and 
   *    handled in milliseconds basis.
   *  - Low Level Initialization
   */
	const char *name = "BlueNRG_7";
	uint8_t AdvAddress[] = {0x08, 0x05, 0x04, 0x03, 0x02, 0x01};	
	uint8_t TxPowerLevel = 7;
	uint16_t AdvInterval = 100;
	
  HAL_Init();
	/* Configure LED0 */
  BSP_LED_Init(LED0);
  /* Configure the system clock */
  SystemClock_Config();
	/* Initialize the BlueNRG SPI driver */
  BNRG_SPI_Init();
  /* Initialize the BlueNRG HCI */
  HCI_Init();
  /* Reset BlueNRG hardware */
  BlueNRG_RST();
	/*Config Adv Parameter And Ready to Adv*/
	ble_start_adv(name, AdvAddress, TxPowerLevel, AdvInterval);
	
  while(1)
  {
    HCI_Process();
    User_Process();

  }
}
/**
	*@brief	 Config adv param and ready to adv
	*@param	 Advname,AdvAddress,TxPowerLevel,Advinterval
	*@retval None
	*/
static void ble_start_adv(char* AdvName, uint8_t*AdvAddress, uint8_t TxPowerLevel, uint16_t AdvInterval)
{
	/*Set Adv Address*/
	Advertise_Address(AdvAddress);
	/*Set Adv Name*/
	ble_device_set_name(AdvName);
	/*Gatt And Gap Init*/
	ble_init_bluenrg();
	/*Set Tx Power Level*/
	ble_device_set_tx_power(TxPowerLevel);
	/* Range: 0x0020 to 0x4000
		 Default: 1.28 s
 		 Time = AdvInterval * 0.625 msec
	*/
	ble_device_set_advertising_interval(AdvInterval);
}


/**
 * @}
 */
 
/**
 * @}
 */

/**
 * @}
 */



/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
