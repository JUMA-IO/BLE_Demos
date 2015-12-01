/**
  ******************************************************************************
  * @file    Projects/Multi/Examples/FreeFallDetection/Src/main.c
  * @author  CL
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   Main program body
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2015 STMicroelectronics</center></h2>
  *
  * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/software_license_agreement_liberty_v2
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
#include "main.h"
#include "osal.h"
#include "debug.h"
#include "cube_hal.h"
#include "stm32_bluenrg_ble.h"
#include "bluenrg_sdk_api.h"
#include <string.h> // strlen
#include <stdio.h>  // sprintf
#include <math.h>   // trunc

/** @defgroup MAIN_Private_Function_Prototypes
 * @{
 */
/* Private function prototypes -----------------------------------------------*/

/** @addtogroup X_NUCLEO_IKS01A1_Examples
  * @{
  */

/** @addtogroup FREE_FALL_DETECTION
  * @{
  */
/* TIM handle declaration */
TIM_HandleTypeDef    TimHandle;
/* Prescaler declaration */
uint32_t uwPrescalerValue = 0;

/* Private variables ---------------------------------------------------------*/
static int free_fall_detected = 0;
static volatile int ff_enable = 0;
static uint32_t delay_time;
volatile uint8_t test_status = 0;

static float humidity[1];
static float temperature[1];


static void hum_temp(void);
void User_Process(void);
static void ble_start_adv(char* AdvName, uint8_t*AdvAddress, uint8_t TxPowerLevel, uint16_t AdvInterval);

int main(void)
{
	const char *name = "BlueNRG_IOT_2";
	uint8_t AdvAddress[] = {0x08, 0x05, 0x04, 0x03, 0x02, 0x02};	
	uint8_t TxPowerLevel = 7;
	uint16_t AdvInterval = 100;
	
  HAL_Init();
	BSP_LED_Init(LED0);
	BSP_LED_On(LED0);
  /* Configure the system clock */
  SystemClock_Config();
	/*Temp*/
	hum_temp();
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

static void hum_temp(void)
{
	/* Initialize the HUM temp */
		while(BSP_HUM_TEMP_isInitialized() != 1){
			HAL_Delay(10);
			BSP_HUM_TEMP_Init();
		}

}

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


void Read_Temp_Hum(void)
{
	uint8_t temp[2] = {0};
	uint16_t temp_1 = 0;
//	/*humity*/
	BSP_HUM_TEMP_GetHumidity(humidity);
	temp_1 = (uint16_t)(humidity[0]*100);
	temp[0] = temp_1 >> 8;
	temp[1] = temp_1 & 0xFF;
	user_send_message(0x01, temp);
	/*Temperature*/
	BSP_HUM_TEMP_GetTemperature(temperature);
	temp_1 = (uint16_t)(temperature[0]*100);
	temp[0] = temp_1 >> 8;
	temp[1] = temp_1 & 0xFF;
	user_send_message(0x00, temp);
	HAL_Delay(1000);
}



/**
 * @brief  This function is executed in case of error occurrence
 * @retval None
 */
void Error_Handler(void)
{
  while(1)
  {}
}

#ifdef  USE_FULL_ASSERT

/**
 * @brief  Reports the name of the source file and the source line number where the assert_param error has occurred
 * @param  file: pointer to the source file name
 * @param  line: assert_param error line source number
 * @retval None
 */
void assert_failed(uint8_t* file, uint32_t line)
{
  /* User can add his own implementation to report the file name and line number,
  ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  
  /* Infinite loop */
  while (1)
  {}
}
#endif

/**
 * @}
 */

/**
 * @}
 */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
