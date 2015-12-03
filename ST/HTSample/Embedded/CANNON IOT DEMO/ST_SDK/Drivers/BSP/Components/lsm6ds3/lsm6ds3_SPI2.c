#include "lsm6ds3_SPI2.h"
#include <math.h>
#include "stm32f4xx_nucleo.h"
#include <string.h>

#define LSM6DS3_DUMMY_BYTE            0xFF    
#define LSM6DS3_NO_RESPONSE_EXPECTED  0x80

static void       LSM6DS3_SPIx_Error(void);


/* SD IO functions */
void              LSM6DS3_SPIx_IO_Init(void);
HAL_StatusTypeDef LSM6DS3_SPIx_IO_WriteCmd(uint8_t Cmd, uint32_t Arg, uint8_t Crc, uint8_t Response);
HAL_StatusTypeDef LSM6DS3_SPIx_IO_WaitResponse(uint8_t Response);
void              LSM6DS3_SPIx_IO_WriteDummy(void);

uint32_t Lsm6ds3_SpixTimeout = LSM6DS3_SPIx_TIMEOUT_MAX; /*<! Value of Timeout when SPI communication fails */
static SPI_HandleTypeDef Lsm6ds3_hnucleo_Spi;
/*
******************************************************************************
                            BUS OPERATIONS
*******************************************************************************/
/**
  * @brief  Initializes SPI MSP.
  * @param  None
  * @retval None
  */
void HAL_SPI_MspInit(SPI_HandleTypeDef *hspi)
{
  GPIO_InitTypeDef  GPIO_InitStruct;  
  
  /*** Configure the GPIOs ***/  
  /* Enable GPIO clock */
  LSM6DS3_SPIx_SCK_GPIO_CLK_ENABLE();
  LSM6DS3_SPIx_MISO_MOSI_GPIO_CLK_ENABLE();
  LSM6DS3_SPIx_CS_GPIO_CLK_ENABLE();
	LSM6DS3_SPIx_IRQ_CLK_ENABLE();
  
  /* Configure SPI SCK */
  GPIO_InitStruct.Pin = LSM6DS3_SPIx_SCK_PIN;
  GPIO_InitStruct.Mode = GPIO_MODE_AF_PP;
  GPIO_InitStruct.Pull  = GPIO_PULLUP;
  GPIO_InitStruct.Speed = GPIO_SPEED_HIGH;
  GPIO_InitStruct.Alternate = LSM6DS3_SPIx_SCK_AF;
  HAL_GPIO_Init(LSM6DS3_SPIx_SCK_GPIO_PORT, &GPIO_InitStruct);

  /* Configure SPI MISO and MOSI */ 
  GPIO_InitStruct.Pin = LSM6DS3_SPIx_MOSI_PIN;
	GPIO_InitStruct.Mode = GPIO_MODE_AF_PP;
	GPIO_InitStruct.Speed = GPIO_SPEED_HIGH;
  GPIO_InitStruct.Alternate = LSM6DS3_SPIx_MISO_MOSI_AF;
  GPIO_InitStruct.Pull  = GPIO_PULLUP;
  HAL_GPIO_Init(LSM6DS3_SPIx_MISO_MOSI_GPIO_PORT, &GPIO_InitStruct);
 
  GPIO_InitStruct.Pin = LSM6DS3_SPIx_MISO_PIN;
	GPIO_InitStruct.Mode = GPIO_MODE_AF_OD;
  HAL_GPIO_Init(LSM6DS3_SPIx_MISO_MOSI_GPIO_PORT, &GPIO_InitStruct);
	
  /* Configure CS_PIN pin */
  GPIO_InitStruct.Pin = LSM6DS3_SPIx_CS_PIN;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_PULLUP;
  GPIO_InitStruct.Speed = GPIO_SPEED_HIGH;
  HAL_GPIO_Init(LSM6DS3_SPIx_CS_GPIO_PORT, &GPIO_InitStruct);
	
	/*Configure SPI IRQ*/
	GPIO_InitStruct.Pin = LSM6DS3_SPIx_IRQ_PIN;
	GPIO_InitStruct.Mode = LSM6DS3_SPIx_IRQ_MODE;
	GPIO_InitStruct.Pull = LSM6DS3_SPIx_IRQ_PULL;
	GPIO_InitStruct.Speed = LSM6DS3_SPIx_IRQ_SPEED;
	GPIO_InitStruct.Alternate = LSM6DS3_SPIx_IRQ_ALTERNATE;
	HAL_GPIO_Init(LSM6DS3_SPIx_IRQ_PORT, &GPIO_InitStruct);
	
	
  /*** Configure the SPI peripheral ***/ 
  /* Enable SPI clock */
  LSM6DS3_SPIx_CLK_ENABLE();
}

/**
  * @brief  Initializes SPI HAL.
  * @param  None
  * @retval None
  */
HAL_StatusTypeDef LSM6DS3_SPIx_Init(void)
{
	HAL_StatusTypeDef ret_val = HAL_OK;
  if(HAL_SPI_GetState(&Lsm6ds3_hnucleo_Spi) == HAL_SPI_STATE_RESET)
  {
		
    /* SPI Config */
    Lsm6ds3_hnucleo_Spi.Instance = LSM6DS3_SPIx;
      /* SPI baudrate is set to 12,5 MHz maximum (PCLK2/SPI_BaudRatePrescaler = 100/8 = 12,5 MHz) 
       to verify these constraints:
          - ST7735 LCD SPI interface max baudrate is 15MHz for write and 6.66MHz for read
            Since the provided driver doesn't use read capability from LCD, only constraint 
            on write baudrate is considered.
          - SD card SPI interface max baudrate is 25MHz for write/read
          - PCLK2 max frequency is 100 MHz 
       */ 
    Lsm6ds3_hnucleo_Spi.Init.BaudRatePrescaler = SPI_BAUDRATEPRESCALER_8;
    Lsm6ds3_hnucleo_Spi.Init.Direction = SPI_DIRECTION_2LINES;
    Lsm6ds3_hnucleo_Spi.Init.CLKPhase = SPI_PHASE_2EDGE;
    Lsm6ds3_hnucleo_Spi.Init.CLKPolarity = SPI_POLARITY_HIGH;
    Lsm6ds3_hnucleo_Spi.Init.CRCCalculation = SPI_CRCCALCULATION_DISABLED;
    Lsm6ds3_hnucleo_Spi.Init.CRCPolynomial = 7;
    Lsm6ds3_hnucleo_Spi.Init.DataSize = SPI_DATASIZE_8BIT;
    Lsm6ds3_hnucleo_Spi.Init.FirstBit = SPI_FIRSTBIT_MSB;
    Lsm6ds3_hnucleo_Spi.Init.NSS = SPI_NSS_SOFT;
    Lsm6ds3_hnucleo_Spi.Init.TIMode = SPI_TIMODE_DISABLED;
    Lsm6ds3_hnucleo_Spi.Init.Mode = SPI_MODE_MASTER;

    //LSM6DS3_SPIx_MspInit(&Lsm6ds3_hnucleo_Spi);
    ret_val = HAL_SPI_Init(&Lsm6ds3_hnucleo_Spi);
  }else{
		ret_val = HAL_ERROR;
	}
	 return ret_val;
}

/**
  * @brief  SPI error treatment function.
  * @param  None
  * @retval None
  */
static void LSM6DS3_SPIx_Error (void)
{
  /* De-initialize the SPI communication BUS */
  HAL_SPI_DeInit(&Lsm6ds3_hnucleo_Spi);
  
  /* Re-Initiaize the SPI communication BUS */
  LSM6DS3_SPIx_Init();
}

/******************************************************************************
                            LINK OPERATIONS
*******************************************************************************/


/**
  * @brief  Writes a byte on the SD.
  * @param  Data: byte to send.
  * @retval None
  */
HAL_StatusTypeDef LSM6DS3_SPIx_IO_WriteByte(uint8_t* pBuffer, uint8_t RegisterAddr,
    uint16_t NumByteToWrite)
{
  /* Send the byte */
  //LSM6DS3_SPIx_Write(Data);
	uint8_t pBufferAddr[20] = {0};
	memcpy(pBufferAddr, &RegisterAddr, 1);
	memcpy(pBufferAddr+1, pBuffer, NumByteToWrite);
	HAL_StatusTypeDef status = HAL_OK;
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_RESET);
  status = HAL_SPI_Transmit(&Lsm6ds3_hnucleo_Spi, pBufferAddr, NumByteToWrite+1, Lsm6ds3_SpixTimeout);

	if(status != HAL_OK)
  {
    /* Execute user timeout callback */
    LSM6DS3_SPIx_Error();
  }
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_SET);
	return status;
}
uint8_t _pBufferAddr[5] = {0x3C,0xF2,0xF2,0x3D,0x3D};
HAL_StatusTypeDef _LSM6DS3_SPIx_IO_WriteByte(uint8_t* pBufferAddr,
    uint16_t NumByteToWrite)
{
	Lsm6ds3_hnucleo_Spi.Init.DataSize = SPI_DATASIZE_16BIT;
	
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_RESET);
	HAL_SPI_Transmit(&Lsm6ds3_hnucleo_Spi, _pBufferAddr, 1, Lsm6ds3_SpixTimeout);
	//HAL_SPI_Transmit(&Lsm6ds3_hnucleo_Spi, _pBufferAddr+1, 1, Lsm6ds3_SpixTimeout);
	
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_SET);
	
}

/**
  * @brief  Reads a byte from the SD.
  * @param  None
  * @retval The received byte.
  */
HAL_StatusTypeDef LSM6DS3_SPIx_IO_ReadByte(uint8_t* pBuffer, uint8_t RegisterAddr,
    uint16_t NumByteToRead )
{
	RegisterAddr = RegisterAddr | 0x80;
	HAL_StatusTypeDef status = HAL_OK;
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_RESET);
	status = HAL_SPI_TransmitReceive(&Lsm6ds3_hnucleo_Spi, &RegisterAddr, pBuffer, NumByteToRead+1, Lsm6ds3_SpixTimeout);
   if(status != HAL_OK)
  {
    /* Execute user timeout callback */
    LSM6DS3_SPIx_Error();
  }
	
	HAL_GPIO_WritePin(LSM6DS3_SPIx_CS_GPIO_PORT,LSM6DS3_SPIx_CS_PIN,GPIO_PIN_SET);
	
	return status;
}