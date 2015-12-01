/**
  ******************************************************************************
  * @file    Serial_CMD.h
  * @author  MEMS Application Team
  * @version V1.3.0
  * @date    28-May-2015
  * @brief   This file contains serial commands code
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2013 STMicroelectronics</center></h2>
  *
  * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/software_license_agreement_liberty_v2
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
  */



/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __SERIAL_CMD_H
#define __SERIAL_CMD_H

/* Exported constants --------------------------------------------------------*/

#define DATALOG_MODE         0
#define DATALOG_FUSION_MODE  1
#define DATALOG_AR_MODE      2


/**********  GENERIC  CMD  (0x00 - 0x0F)  **********/
#define CMD_Ping                                                                0x01
#define CMD_Read_PresString                                                     0x02
#define CMD_NACK                                                                0x03
#define CMD_CheckModeSupport                                                    0x04
#define CMD_UploadAR                                                            0x05
#define CMD_Start_Data_Streaming                                                0x08
#define CMD_Stop_Data_Streaming                                                 0x09
#define CMD_StartDemo                                                           0x0A
#define CMD_Sleep_Sec                                                           0x0B
#define CMD_Set_DateTime                                                        0x0C
#define CMD_Get_DateTime                                                        0x0D
#define CMD_Enter_DFU_Mode                                                      0x0E
#define CMD_Reset                                                               0x0F

#define CMD_Reply_Add                                                           0x80

/****************************************************/


/******** ENVIRONMENTAL  CMD  (0x60 - 0x6F)  ********/

#define CMD_LPS25H_Init                                                         0x60
#define CMD_LPS25H_Read                                                         0x61
#define CMD_HTS221_Init                                                         0x62
#define CMD_HTS221_Read                                                         0x63

/****************************************************/


/******** INERTIAL  CMD  (0x70 - 0x/7F)  ********/

#define CMD_LSM9DS1_Init                                                        0x70
#define CMD_LSM9DS1_9AXES_Read                                                  0x71
#define CMD_LSM9DS1_AHRS_Read                                                   0x72
#define CMD_LSM9DS1_ACC_Read                                                    0x73
#define CMD_LSM9DS1_GYR_Read                                                    0x74
#define CMD_LSM9DS1_MAG_Read                                                    0x75
#define CMD_LSM6DSO_Init                                                        0x76
#define CMD_LSM6DSO_ACC_Read                                                    0x77
#define CMD_LSM6DSO_GYR_Read                                                    0x78
#define CMD_LSM6DS0_6AXES_Read                                                  0x79
#define CMD_LIS3MDL_Init                                                        0x7A
#define CMD_LIS3MDL_Read                                                        0x7B
#define CMD_SF_Init                                                             0x7C
#define CMD_SF_Data                                                             0x7D

/****************************************************/

#endif /* __SERIAL_CMD_H */


/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
