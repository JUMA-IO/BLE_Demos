/**
  ******************************************************************************
  * @file    serial_protocol.c
  * @author  AST Robotics group
  * @version Version 1.0
  * @date    25 Sept 2008
  * @brief   This file implements some utilities for the serial protocol
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


/* Standard include ----------------------------------------------------------*/
#include "serial_protocol.h"

/** @addtogroup MIDDLEWARES
  * @{
  */

/** @defgroup SERIAL_UTILITY SERIAL_UTILITY
  * @{
  */

/** @defgroup SERIAL_PROTOCOL SERIAL_PROTOCOL
  * @{
  */

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
/* Private function prototypes -----------------------------------------------*/
/* Private functions ---------------------------------------------------------*/

/** @defgroup SERIAL_PROTOCOL_Exported_Functions SERIAL_PROTOCOL_Exported_Functions
 * @{
 */
/**
 * @brief  Byte stuffing process for one byte
 * @param  Dest destination
 * @param  Source source
 * @retval Total number of bytes processed
*/
int ByteStuffCopyByte(uint8_t *Dest, uint8_t Source)
{
  switch(Source)
  {
    case TMsg_EOF:
      Dest[0] = TMsg_BS;
      Dest[1] = TMsg_BS_EOF;
      return 2;
    case TMsg_BS:
      Dest[0] = TMsg_BS;
      Dest[1] = TMsg_BS;
      return 2;
    default:
      Dest[0] = Source;
      return 1;
  }
}

/**
 * @brief  Byte stuffing process for a Msg
 * @param  Dest destination
 * @param  Source source
 * @retval Total number of bytes processed
 */
int ByteStuffCopy(uint8_t *Dest, TMsg *Source)
{
  int i, Count;
  
  Count = 0;
  for (i = 0; i < Source->Len; i++)
  {
    Count += ByteStuffCopyByte(&Dest[Count], Source->Data[i]);
  }
  Dest[Count] = TMsg_EOF;
  Count++;
  return Count;
}

/**
 * @brief  Reverse Byte stuffing process for one byte
 * @param  Source source
 * @param  Dest destination
 * @retval Number of input bytes processed (1 or 2) or 0 for invalid sequence
 */
int ReverseByteStuffCopyByte(uint8_t *Source, uint8_t *Dest)
{
  if (Source[0] == TMsg_BS)
  {
    if (Source[1] == TMsg_BS)
    {
      *Dest = TMsg_BS;
      return 2;
    }
    if (Source[1] == TMsg_BS_EOF)
    {
      *Dest = TMsg_EOF;
      return 2;
    }
    return 0; // invalide sequence
  }
  else
  {
    *Dest = Source[0];
    return 1;
  }
}

/**
 * @brief  Reverse Byte stuffing process for two input data
 * @param  Source0 input data
 * @param  Source1 input data
 * @param  Dest the destination data
 * @retval Number of input bytes processed (1 or 2) or 0 for invalid sequence
 */
int ReverseByteStuffCopyByte2(uint8_t Source0, uint8_t Source1, uint8_t *Dest)
{
  if (Source0 == TMsg_BS)
  {
    if (Source1 == TMsg_BS)
    {
      *Dest = TMsg_BS;
      return 2;
    }
    if (Source1 == TMsg_BS_EOF)
    {
      *Dest = TMsg_EOF;
      return 2;
    }
    return 0; // invalid sequence
  }
  else
  {
    *Dest = Source0;
    return 1;
  }
}

/**
 * @brief  Reverse Byte stuffing process for a Msg
 * @param  Dest destination
 * @param  Source source
 * @retval 1 if the operation succeeds, 0 if an error occurs
 */
int ReverseByteStuffCopy(TMsg *Dest, uint8_t *Source)
{
  int Count = 0, State = 0;
  
  while ((*Source) != TMsg_EOF)
  {
    if (State == 0)
    {
      if ((*Source) == TMsg_BS)
      {
        State = 1;
      }
      else
      {
        Dest->Data[Count] = *Source;
        Count++;
      }
    }
    else
    {
      if ((*Source) == TMsg_BS)
      {
        Dest->Data[Count] = TMsg_BS;
        Count++;
      }
      else
      {
        if ((*Source) == TMsg_BS_EOF)
        {
          Dest->Data[Count] = TMsg_EOF;
          Count++;
        }
        else
        {
          return 0; // invalid sequence
        }
      }
      State = 0;
    }
    Source++;
  }
  if (State != 0) return 0;
  Dest->Len = Count;
  return 1;
}

/**
 * @brief  Compute and add checksum
 * @param  Msg pointer to the message
 * @retval None
 */
void CHK_ComputeAndAdd(TMsg *Msg)
{
  uint8_t CHK = 0;
  int i;
  
  for(i = 0; i < Msg->Len; i++)
  {
    CHK -= Msg->Data[i];
  }
  Msg->Data[i] = CHK;
  Msg->Len++;
}

/**
 * @brief  Compute and remove checksum
 * @param  Msg pointer to the message
 * @retval A number different from 0 if the operation succeeds, 0 if an error occurs
 */
int CHK_CheckAndRemove(TMsg *Msg)
{
  uint8_t CHK = 0;
  int i;
  
  for(i = 0; i < Msg->Len; i++)
  {
    CHK += Msg->Data[i];
  }
  Msg->Len--;
  return (CHK == 0);
}

/**
 * @brief  Build an array from the uint32_t (LSB first)
 * @param  Dest destination
 * @param  Source source
 * @param  Len number of bytes
 * @retval None
 */
void Serialize(uint8_t *Dest, uint32_t Source, uint32_t Len)
{
  int i;
  for (i = 0; i < Len; i++)
  {
    Dest[i] = Source & 0xFF;
    Source >>= 8;
  }
}

/**
 * @brief  Unbuild a Number from an array (LSB first)
 * @param  Source source
 * @param  Len number of bytes
 * @retval Rebuild unsigned int variable
 */
uint32_t Deserialize(uint8_t *Source, uint32_t Len)
{
  uint32_t app;
  app = Source[--Len];
  while(Len > 0)
  {
    app <<= 8;
    app += Source[--Len];
  }
  return app;
}

/**
 * @brief  Build an array from the uint32_t (LSB first)
 * @param  Dest destination
 * @param  Source source
 * @param  Len number of bytes
 * @retval None
 */
void Serialize_s32(uint8_t *Dest, int32_t Source, uint32_t Len)
{
  int i;
  for (i = 0; i < Len; i++)
  {
    Dest[i] = Source & 0xFF;
    Source >>= 8;
  }
}

/**
 * @brief  Unbuild a Number from an array (LSB first)
 * @param  Source source
 * @param  Len number of bytes
 * @retval Rebuild signed int variable
 */
int32_t Deserialize_s32(uint8_t *Source, uint32_t Len)
{
  int32_t app;
  app = Source[--Len];
  while(Len > 0)
  {
    app <<= 8;
    app += Source[--Len];
  }
  return app;
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

/**
  * @}
  */

/******************* (C) COPYRIGHT 2007 STMicroelectronics *****END OF FILE****/
