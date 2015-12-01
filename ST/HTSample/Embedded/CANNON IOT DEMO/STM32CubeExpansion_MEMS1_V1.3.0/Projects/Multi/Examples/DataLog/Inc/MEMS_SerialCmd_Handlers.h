/**
  *******************************************************************************
  * @file    Projects/Multi/Examples/DataLog/Inc/MEMS_SerialCmd_Handlers.h
  * @author  MEMS Application Team
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   header for MEMS_SerialCmd_Handlers.c.
  *******************************************************************************
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
  ********************************************************************************
  */


/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __MEMS_SERIALCMD_HANDLERS
#define __MEMS_SERIALCMD_HANDLERS

#ifdef __cplusplus
extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "cube_hal.h"
#include "serial_protocol.h"

typedef enum
{
  MEMS_SENSOR_OK = 0,
  MEMS_SENSOR_ERROR = 1,
  MEMS_SENSOR_TIMEOUT = 2
} MEMS_Sensor_StatusTypeDef;

/* Imu6Axes Sensor Handlers */
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_Init(TMsg *Msg);
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_ACC_Read(TMsg *Msg);
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_GYR_Read(TMsg *Msg);
MEMS_Sensor_StatusTypeDef Handle_CMD_LSM6DS0_6AXES_Read(TMsg *Msg);

/* Magneto Sensor Handlers */
MEMS_Sensor_StatusTypeDef Handle_CMD_LIS3MDL_Init(TMsg *Msg);
MEMS_Sensor_StatusTypeDef Handle_CMD_LIS3MDL_MAG_Read(TMsg *Msg);

/* Imu6Axes + Magneto Sensor Handlers */
MEMS_Sensor_StatusTypeDef Handle_CMD_9AXES_Read(TMsg *Msg);

#ifdef __cplusplus
}
#endif

#endif /* __MEMS_SERIALCMD_HANDLERS */



/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
