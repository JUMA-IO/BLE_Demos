/**
  ******************************************************************************
  * @file    serial_protocol.h
  * @author  AST Robotics group
  * @version V1.0.0
  * @date    25 Sept 2008
  * @brief   This file contains serial protocol code
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

/* Define to prevent recursive inclusion ------------------------------------ */
#ifndef __SERIAL_PROTOCOL__
#define __SERIAL_PROTOCOL__

/* Includes ------------------------------------------------------------------*/
#include <stdint.h>
/** @addtogroup MIDDLEWARES
  * @{
  */

/** @defgroup SERIAL_UTILITY SERIAL_UTILITY
  * @{
  */

/** @defgroup SERIAL_PROTOCOL SERIAL_PROTOCOL
  * @{
  */

/* Exported defines --------------------------------------------------------*/

/** @defgroup SERIAL_PROTOCOL_Exported_Defines SERIAL_PROTOCOL_Exported_Defines
  * @{
  */

#define TMsg_EOF                            0xF0
#define TMsg_BS                             0xF1
#define TMsg_BS_EOF                         0xF2


#ifdef USE_USB_OTG_HS
#define TMsg_MaxLen             512
#else
#define TMsg_MaxLen             256
#endif
/**
  * @}
  */


/* Exported types ------------------------------------------------------------*/

/** @defgroup SERIAL_PROTOCOL_Types SERIAL_PROTOCOL_Types
  * @{
  */
/**
 * @brief  Serial message structure definition
 */
typedef struct
{
  uint32_t Len;
  uint8_t Data[TMsg_MaxLen];
} TMsg;
/**
  * @}
  */

/* Exported macro ------------------------------------------------------------*/

/* Private functions ---------------------------------------------------------*/

/* Exported functions ------------------------------------------------------- */

/** @defgroup SERIAL_PROTOCOL_Exported_Functions SERIAL_PROTOCOL_Exported_Functions
 * @{
 */
int ByteStuffCopyByte(uint8_t *Dest, uint8_t Source);
int ReverseByteStuffCopyByte2(uint8_t Source0, uint8_t Source1, uint8_t *Dest);
int ByteStuffCopy(uint8_t *Dest, TMsg *Source);
int ReverseByteStuffCopyByte(uint8_t *Source, uint8_t *Dest);
int ReverseByteStuffCopy(TMsg *Dest, uint8_t *Source);
void CHK_ComputeAndAdd(TMsg *Msg);
int CHK_CheckAndRemove(TMsg *Msg);
uint32_t Deserialize(uint8_t *Source, uint32_t Len);
int32_t Deserialize_s32(uint8_t *Source, uint32_t Len);
void Serialize(uint8_t *Dest, uint32_t Source, uint32_t Len);
void Serialize_s32(uint8_t *Dest, int32_t Source, uint32_t Len);

/**
  * @}
  */

/**
  * @}
  */

/**
  * @}
  */

/**
  * @}
  */

#endif /* __SERIAL_PROTOCOL__ */

/******************* (C) COPYRIGHT 2007 STMicroelectronics *****END OF FILE****/
