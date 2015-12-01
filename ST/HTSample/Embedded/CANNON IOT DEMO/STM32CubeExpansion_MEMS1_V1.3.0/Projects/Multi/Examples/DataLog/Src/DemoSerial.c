/**
  ******************************************************************************
  * @file    Projects/Multi/Examples/DataLog/Src/DemoSerial.c
  * @author  MEMS Application Team
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   Handler AST Serial Protocol
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
#include "DemoSerial.h"
#include "Env_SerialCmd_Handlers.h"
#include "MEMS_SerialCmd_Handlers.h"
#include "main.h"
#include "com.h"

/** @addtogroup X_NUCLEO_IKS01A1_Examples
  * @{
  */

/** @addtogroup DATALOG
  * @{
  */

/* Extern variables ----------------------------------------------------------*/
extern volatile uint32_t Sensors_Enabled;
extern volatile uint32_t DataTxPeriod;

/* Private variables ---------------------------------------------------------*/
volatile uint8_t DataLoggerActive;
volatile uint8_t SenderInterface = 0;
uint8_t PresentationString[] = {"MEMS shield demo"};
volatile uint8_t DataStreamingDest = 1;


/**
  * @brief  Build the reply header
  * @param  Msg the pointer to the message to be built
  * @retval None
  */
void BUILD_REPLY_HEADER(TMsg *Msg)
{
  Msg->Data[0] = Msg->Data[1];
  Msg->Data[1] = DEV_ADDR;
  Msg->Data[2] += CMD_Reply_Add;
}

/**
  * @brief  Build the nack header
  * @param  Msg the pointer to the message to be built
  * @retval None
  */
void BUILD_NACK_HEADER(TMsg *Msg)
{
  Msg->Data[0] = Msg->Data[1];
  Msg->Data[1] = DEV_ADDR;
  Msg->Data[2] = CMD_NACK;
}

/**
  * @brief  Initialize the streaming header
  * @param  Msg the pointer to the header to be initialized
  * @retval None
  */
void INIT_STREAMING_HEADER(TMsg *Msg)
{
  Msg->Data[0] = DataStreamingDest;
  Msg->Data[1] = DEV_ADDR;
  Msg->Data[2] = CMD_Start_Data_Streaming;
  Msg->Len = 3;
}

/**
  * @brief  Initialize the streaming message
  * @param  Msg the pointer to the message to be initialized
  * @retval None
  */
void INIT_STREAMING_MSG(TMsg *Msg)
{
  uint8_t i;
  
  Msg->Data[0] = DataStreamingDest;
  Msg->Data[1] = DEV_ADDR;
  Msg->Data[2] = CMD_Start_Data_Streaming;
  for(i = 3; i < STREAMING_MSG_LENGTH + 3; i++)
  {
    Msg->Data[i] = 0;
  }
  Msg->Len = 3;
  
}

/**
  * @brief  Handle a message
  * @param  Msg the pointer to the message to be handled
  * @retval 1 if the message is correctly handled, 0 otherwise
  */
int HandleMSG(TMsg *Msg)
//  DestAddr | SouceAddr | CMD | PAYLOAD
//      1          1        1       N
{
  uint32_t i;
  
  if (Msg->Len < 2) return 0;
  if (Msg->Data[0] != DEV_ADDR) return 0;
  switch (Msg->Data[2])   // CMD
  {
  
    case CMD_Ping:
      if (Msg->Len != 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      Msg->Len = 3;
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_Enter_DFU_Mode:
      if (Msg->Len != 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      Msg->Len = 3;
      return 1;
      
    case CMD_Read_PresString:
      if (Msg->Len != 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      i = 0; //
      while (i < (sizeof(PresentationString) - 1))
      {
        Msg->Data[3 + i] = PresentationString[i];
        i++;
      }
      
      Msg->Len = 3 + i;
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_CheckModeSupport:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      Serialize_s32(&Msg->Data[3], DATALOG_MODE, 4);
      Msg->Len = 3 + 4;
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_LPS25H_Init:
      if (Msg->Len < 3) return 0;
      
      if(Handle_CMD_LPS25H_Init(Msg) == ENV_SENSOR_OK)
      {
        PRESSURE_ComponentTypeDef ret;
        BUILD_REPLY_HEADER(Msg);
        ret = BSP_PRESSURE_GetComponentType();
        switch (ret)
        {
          case PRESSURE_LPS25H_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)PRESSURE_LPS25H_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          case PRESSURE_LPS25HB_DIL24_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)PRESSURE_LPS25HB_DIL24_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          default:
            break;
        }
      }
      else
      {
        BUILD_NACK_HEADER(Msg);
      }
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_LPS25H_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_LPS25H_Read(Msg) == ENV_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    case CMD_HTS221_Init:
      if (Msg->Len < 3) return 0;
      
      if(Handle_CMD_HTS221_Init(Msg) == ENV_SENSOR_OK)
      {
        HUM_TEMP_ComponentTypeDef ret;
        BUILD_REPLY_HEADER(Msg);
        ret = BSP_HUM_TEMP_GetComponentType();
        switch (ret)
        {
          case HUM_TEMP_HTS221_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)HUM_TEMP_HTS221_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          default:
            break;
        }
      }
      else
      {
        BUILD_NACK_HEADER(Msg);
      }
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_HTS221_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_HTS221_Read(Msg) == ENV_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    // for future use
    case CMD_LSM9DS1_Init:
    case CMD_LSM9DS1_ACC_Read:
    case CMD_LSM9DS1_GYR_Read:
    case CMD_LSM9DS1_MAG_Read:
    case CMD_LSM9DS1_9AXES_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_LSM6DSO_Init:
      if (Msg->Len < 3) return 0;
      if(Handle_CMD_LSM6DS0_Init(Msg) == MEMS_SENSOR_OK)
      {
        IMU_6AXES_ComponentTypeDef ret;
        BUILD_REPLY_HEADER(Msg);
        ret = BSP_IMU_6AXES_GetComponentType();
        switch (ret)
        {
          case IMU_6AXES_LSM6DS0_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)IMU_6AXES_LSM6DS0_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          case IMU_6AXES_LSM6DS3_DIL24_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)IMU_6AXES_LSM6DS3_DIL24_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          default:
            break;
        }
      }
      else
      {
        BUILD_NACK_HEADER(Msg);
      }
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_LSM6DSO_ACC_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_LSM6DS0_ACC_Read(Msg) == MEMS_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    case CMD_LSM6DSO_GYR_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_LSM6DS0_GYR_Read(Msg) == MEMS_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    case CMD_LSM6DS0_6AXES_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_LSM6DS0_6AXES_Read(Msg) == MEMS_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    case CMD_LIS3MDL_Init:
      if (Msg->Len < 3) return 0;
      
      if(Handle_CMD_LIS3MDL_Init(Msg) == MEMS_SENSOR_OK)
      {
        MAGNETO_ComponentTypeDef ret;
        BUILD_REPLY_HEADER(Msg);
        ret = BSP_MAGNETO_GetComponentType();
        switch (ret)
        {
          case MAGNETO_LIS3MDL_COMPONENT:
            Serialize_s32(&Msg->Data[3], (int32_t)MAGNETO_LIS3MDL_COMPONENT, 4);
            Msg->Len = 3 + 4;
            break;
          default:
            break;
        }
      }
      else
      {
        BUILD_NACK_HEADER(Msg);
      }
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_LIS3MDL_Read:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      if(Handle_CMD_LIS3MDL_MAG_Read(Msg) == MEMS_SENSOR_OK)
      {
        UART_SendMsg(Msg);
      }
      return 1;
      
    case CMD_Start_Data_Streaming:
      if (Msg->Len < 3) return 0;
      Sensors_Enabled = Deserialize(&Msg->Data[3], 4);
      DataTxPeriod = Deserialize(&Msg->Data[7], 4);
      DataLoggerActive = 1;
      DataStreamingDest = Msg->Data[1];
      BUILD_REPLY_HEADER(Msg);
      Msg->Len = 3;
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_Stop_Data_Streaming:
      if (Msg->Len < 3) return 0;
      Sensors_Enabled = 0;
      DataLoggerActive = 0;
      BUILD_REPLY_HEADER(Msg);
      UART_SendMsg(Msg);
      return 1;
      
    case CMD_Set_DateTime:
      if (Msg->Len < 3) return 0;
      BUILD_REPLY_HEADER(Msg);
      Msg->Len = 3;
      RTC_TimeRegulate(Msg->Data[3], Msg->Data[4], Msg->Data[5]);
      UART_SendMsg(Msg);
      return 1;
      
    default:
      return 0;
  }
}

/**
 * @}
 */

/**
 * @}
 */
