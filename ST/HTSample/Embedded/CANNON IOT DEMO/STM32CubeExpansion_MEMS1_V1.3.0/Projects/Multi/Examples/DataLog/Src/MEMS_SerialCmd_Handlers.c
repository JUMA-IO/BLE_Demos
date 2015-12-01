/**
  ******************************************************************************
  * @file    Projects/Multi/Examples/DataLog/Src/MEMS_SerialCmd_Handlers.c
  * @author  MEMS Application Team
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   This file provides set of firmware functions to handle AST serial
  *          commands for MEMS sensors.
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
#include "MEMS_SerialCmd_Handlers.h"
#include "x_nucleo_iks01a1_imu_6axes.h"
#include "x_nucleo_iks01a1_magneto.h"

/** @addtogroup X_NUCLEO_IKS01A1_Examples
  * @{
  */

/** @addtogroup DATALOG
  * @{
  */

/* Extern variables ----------------------------------------------------------*/
extern volatile Axes_TypeDef ACC_Value;
extern volatile Axes_TypeDef GYR_Value;
extern volatile Axes_TypeDef MAG_Value;

/**
  * @brief  Handle IMU 6 axes sensor initialization command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_Init(TMsg *Msg)
{

  IMU_6AXES_StatusTypeDef sensorStatus = IMU_6AXES_ERROR;
  
  sensorStatus = BSP_IMU_6AXES_Init();
  
  if (sensorStatus == IMU_6AXES_OK)
    return MEMS_SENSOR_OK;
    
  else if (sensorStatus == IMU_6AXES_TIMEOUT)
    return MEMS_SENSOR_TIMEOUT;
    
  else
    return MEMS_SENSOR_ERROR;
}

/**
  * @brief  Handle accelerometer read command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_ACC_Read(TMsg *Msg)
{
  Serialize_s32(&Msg->Data[3], (int32_t)ACC_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[7], (int32_t)ACC_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[11], (int32_t)ACC_Value.AXIS_Z, 4);
  
  Msg->Len = 3 + 4 + 4 + 4;
  return MEMS_SENSOR_OK;
}

/**
  * @brief  Handle gyroscope read command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_GYR_Read(TMsg *Msg)
{
  Serialize_s32(&Msg->Data[3], (int32_t)GYR_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[7], (int32_t)GYR_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[11], (int32_t)GYR_Value.AXIS_Z, 4);
  
  Msg->Len = 3 + 4 + 4 + 4;
  return MEMS_SENSOR_OK;
}

/**
  * @brief  Handle IMU 6 axes sensor read command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_6AXES_Read(TMsg *Msg)
{
  Serialize_s32(&Msg->Data[3], (int32_t)ACC_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[7], (int32_t)ACC_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[11], (int32_t)ACC_Value.AXIS_Z, 4);
  
  Serialize_s32(&Msg->Data[15], (int32_t)GYR_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[19], (int32_t)GYR_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[23], (int32_t)GYR_Value.AXIS_Z, 4);
  
  Msg->Len = 3 + 12 + 12;
  return MEMS_SENSOR_OK;
}


/**
  * @brief  Handle magnetometer sensor initialization command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LIS3MDL_Init(TMsg *Msg)
{

  MAGNETO_StatusTypeDef sensorStatus = MAGNETO_ERROR;
  
  sensorStatus = BSP_MAGNETO_Init();
  
  if (sensorStatus == MAGNETO_OK)
    return MEMS_SENSOR_OK;
    
  else if (sensorStatus == MAGNETO_TIMEOUT)
    return MEMS_SENSOR_TIMEOUT;
    
  else
    return MEMS_SENSOR_ERROR;
}

/**
  * @brief  Handle magnetometer sensor read command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_LIS3MDL_MAG_Read(TMsg *Msg)
{
  Serialize_s32(&Msg->Data[3], (int32_t)MAG_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[7], (int32_t)MAG_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[11], (int32_t)MAG_Value.AXIS_Z, 4);
  
  Msg->Len = 3 + 4 + 4 + 4;
  return MEMS_SENSOR_OK;
}


/**
  * @brief  Handle 9 axes sensor read command
  * @param  Msg the pointer to the message to be handled
  * @retval MEMS_SENSOR_OK if success, MEMS_SENSOR_ERROR if error occurs, MEMS_SENSOR_TIMEOUT if timeout occurs
  */
MEMS_Sensor_StatusTypeDef Handle_CMD_9AXES_Read(TMsg *Msg)
{

  Serialize_s32(&Msg->Data[3], (int32_t)ACC_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[7], (int32_t)ACC_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[11], (int32_t)ACC_Value.AXIS_Z, 4);
  
  Serialize_s32(&Msg->Data[15], (int32_t)GYR_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[19], (int32_t)GYR_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[23], (int32_t)GYR_Value.AXIS_Z, 4);
  
  Serialize_s32(&Msg->Data[27], (int32_t)MAG_Value.AXIS_X, 4);
  Serialize_s32(&Msg->Data[31], (int32_t)MAG_Value.AXIS_Y, 4);
  Serialize_s32(&Msg->Data[35], (int32_t)MAG_Value.AXIS_Z, 4);
  
  Msg->Len = 3 + 12 + 12 + 12;
  return MEMS_SENSOR_OK;
  
}
/**
  * @}
  */

/**
 * @}
 */
