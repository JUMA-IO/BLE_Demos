/**
  ******************************************************************************
  * @file    Projects/Multi/Examples/DataLog/Src/Env_SerialCmd_Handlers.c
  * @author  AST Robotics Team
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   This file provides set of firmware functions to handle AST serial
  *          commands for environmental sensors.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2014 STMicroelectronics</center></h2>
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
#include "Env_SerialCmd_Handlers.h"
#include "main.h"

/** @addtogroup X_NUCLEO_IKS01A1_Examples
  * @{
  */

/** @addtogroup DATALOG
  * @{
  */

/* Extern variables ----------------------------------------------------------*/
extern volatile float PRESSURE_Value;
extern volatile float HUMIDITY_Value;
extern volatile float TEMPERATURE_Value;

/**
  * @brief  Handle pressure sensor initialization command
  * @param  Msg the pointer to the message to be handled
  * @retval ENV_SENSOR_OK if success, ENV_SENSOR_ERROR if error occurs, ENV_SENSOR_TIMEOUT if timeout occurs
  */
Env_Sensor_StatusTypeDef Handle_CMD_LPS25H_Init(TMsg *Msg)
{

  PRESSURE_StatusTypeDef sensorStatus = PRESSURE_ERROR;
  
  sensorStatus = BSP_PRESSURE_Init();
  
  if (sensorStatus == PRESSURE_OK)
    return ENV_SENSOR_OK;
    
  else if (sensorStatus == PRESSURE_TIMEOUT)
    return ENV_SENSOR_TIMEOUT;
    
  else
    return ENV_SENSOR_ERROR;
}

/**
  * @brief  Handle pressure sensor read command
  * @param  Msg the pointer to the message to be handled
  * @retval ENV_SENSOR_OK if success, ENV_SENSOR_ERROR if error occurs, ENV_SENSOR_TIMEOUT if timeout occurs
  */
Env_Sensor_StatusTypeDef Handle_CMD_LPS25H_Read(TMsg *Msg)
{
  uint32_t tempInt;
  uint8_t tempFract;
  
  tempInt = (uint32_t)PRESSURE_Value * 100;
  tempFract = (uint32_t)((float)PRESSURE_Value * 100.0f - tempInt);
  tempInt /= 100;
  
  Serialize(&Msg->Data[3], tempInt, 2);    // Integer part of pressure Dec-21
  Msg->Data[5] = tempFract;  // Pressure Fractional part
  
  Msg->Data[7] = 0x00;
  Msg->Len = 3 + 4 + 1;
  return ENV_SENSOR_OK;
}

/**
  * @brief  Handle humidity and temperature sensor initialization command
  * @param  Msg the pointer to the message to be handled
  * @retval ENV_SENSOR_OK if success, ENV_SENSOR_ERROR if error occurs, ENV_SENSOR_TIMEOUT if timeout occurs
  */
Env_Sensor_StatusTypeDef Handle_CMD_HTS221_Init(TMsg *Msg)
{

  HUM_TEMP_StatusTypeDef sensorStatus = HUM_TEMP_ERROR;
  
  sensorStatus = BSP_HUM_TEMP_Init();
  
  if (sensorStatus == HUM_TEMP_OK)
    return ENV_SENSOR_OK;
    
  else if (sensorStatus == HUM_TEMP_TIMEOUT)
    return ENV_SENSOR_TIMEOUT;
    
  else
    return ENV_SENSOR_ERROR;
}

/**
  * @brief  Handle humidity and temperature sensor read command
  * @param  Msg the pointer to the message to be handled
  * @retval ENV_SENSOR_OK if success, ENV_SENSOR_ERROR if error occurs, ENV_SENSOR_TIMEOUT if timeout occurs
  */
Env_Sensor_StatusTypeDef Handle_CMD_HTS221_Read(TMsg *Msg)
{
  Msg->Data[3] = (uint8_t)HUMIDITY_Value;
  Msg->Data[4] = (uint8_t)((HUMIDITY_Value * 100) - (Msg->Data[3] * 100));
  
  Msg->Data[5] = (uint8_t)TEMPERATURE_Value;
  Msg->Data[6] = (uint8_t)((TEMPERATURE_Value * 100) - (Msg->Data[5] * 100));
  
  Msg->Data[7] = 0x00;
  Msg->Len = 3 + 4 + 1;
  
  return ENV_SENSOR_OK;
}

/**
  * @}
  */

/**
 * @}
 */
