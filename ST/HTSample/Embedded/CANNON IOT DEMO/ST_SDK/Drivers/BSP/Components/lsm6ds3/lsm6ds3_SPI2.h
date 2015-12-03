#ifndef _LSM6DS3_SPI2_H_
#define _LSM6DS3_SPI2_H_
//#include "lsm6ds3.h"
#include "stm32f4xx_hal_def.h"
#include "stm32f4xx_hal_spi.h"

HAL_StatusTypeDef  LSM6DS3_SPIx_Init(void);
HAL_StatusTypeDef    LSM6DS3_SPIx_IO_ReadByte(uint8_t* pBuffer, uint8_t RegisterAddr,
    uint16_t NumByteToRead );
HAL_StatusTypeDef LSM6DS3_SPIx_IO_WriteByte(uint8_t* pBuffer, uint8_t RegisterAddr,
    uint16_t NumByteToWrite);
HAL_StatusTypeDef _LSM6DS3_SPIx_IO_WriteByte(uint8_t* pBufferAddr,
    uint16_t NumByteToWrite);
		
void LSM6DS3_SPIx_MspInit(SPI_HandleTypeDef *hspi);
#endif //_LSM6DS3_SPI2_H_

