/**
  ******************************************************************************
  * @file    stm32f4xx_nucleo_bluenrg.c
  * @author  MCD Application Team
  * @version V1.1.0
  * @date    19-June-2014
  * @cond DOXYGEN_EXCLUDE
  * @brief   
  * @endcond
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2014 STMicroelectronics</center></h2>
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
#include "stm32_bluenrg_ble_dma_lp.h"

/** @addtogroup BSP
  * @{
  */ 

/** @addtogroup STM32F4XX_NUCLEO
  * @{
  */   
    
/** @defgroup STM32F4XX_NUCLEO_BLUENRG_DMA_LP
  * @{
  */ 

/** @defgroup STM32F4XX_NUCLEO_BLUENRG_DMA_LP_Exported_Functions
  * @{
  */ 
  
/**
 * @brief  This function is used for low level initialization of the SPI 
 *         communication with the BlueNRG Expansion Board.
 * @param  Pointer to the handle of the STM32Cube HAL SPI interface.
 * @retval None
 */
void HAL_SPI_MspInit(SPI_HandleTypeDef* hspi)
{
  uint32_t peripheral_address;
  
  GPIO_InitTypeDef GPIO_InitStruct;
  if(hspi->Instance==BNRG_SPI_INSTANCE)
  {
    /* Enable peripherals clock */
    
    /* Enable GPIO Ports Clock */  
    BNRG_SPI_SCLK_CLK_ENABLE();
    BNRG_SPI_MISO_CLK_ENABLE();
    BNRG_SPI_MOSI_CLK_ENABLE();
    BNRG_SPI_CS_CLK_ENABLE();
    BNRG_SPI_IRQ_CLK_ENABLE();
    
    /* Enable SPI clock */
    BNRG_SPI_CLK_ENABLE();
    
    /* SCLK */
    GPIO_InitStruct.Pin = BNRG_SPI_SCLK_PIN;
    GPIO_InitStruct.Mode = BNRG_SPI_SCLK_MODE;
    GPIO_InitStruct.Pull = BNRG_SPI_SCLK_PULL;
    GPIO_InitStruct.Speed = BNRG_SPI_SCLK_SPEED;
    GPIO_InitStruct.Alternate = BNRG_SPI_SCLK_ALTERNATE;
    HAL_GPIO_Init(BNRG_SPI_SCLK_PORT, &GPIO_InitStruct); 
    
    /* MISO */
    GPIO_InitStruct.Pin = BNRG_SPI_MISO_PIN;
    GPIO_InitStruct.Mode = BNRG_SPI_MISO_MODE;
    GPIO_InitStruct.Pull = BNRG_SPI_MISO_PULL;
    GPIO_InitStruct.Speed = BNRG_SPI_MISO_SPEED;
    GPIO_InitStruct.Alternate = BNRG_SPI_MISO_ALTERNATE;
    HAL_GPIO_Init(BNRG_SPI_MISO_PORT, &GPIO_InitStruct);
    
    /* MOSI */
    GPIO_InitStruct.Pin = BNRG_SPI_MOSI_PIN;
    GPIO_InitStruct.Mode = BNRG_SPI_MOSI_MODE;
    GPIO_InitStruct.Pull = BNRG_SPI_MOSI_PULL;
    GPIO_InitStruct.Speed = BNRG_SPI_MOSI_SPEED;
    GPIO_InitStruct.Alternate = BNRG_SPI_MOSI_ALTERNATE;
    HAL_GPIO_Init(BNRG_SPI_MOSI_PORT, &GPIO_InitStruct);
    
    /* NSS/CSN/CS */
    GPIO_InitStruct.Pin = BNRG_SPI_CS_PIN;
    GPIO_InitStruct.Mode = BNRG_SPI_CS_MODE;
    GPIO_InitStruct.Pull = BNRG_SPI_CS_PULL;
    GPIO_InitStruct.Speed = BNRG_SPI_CS_SPEED;
    GPIO_InitStruct.Alternate = BNRG_SPI_CS_ALTERNATE;
    HAL_GPIO_Init(BNRG_SPI_CS_PORT, &GPIO_InitStruct);
    HAL_GPIO_WritePin(BNRG_SPI_CS_PORT, BNRG_SPI_CS_PIN, GPIO_PIN_SET);
    
    /* IRQ -- INPUT */
    GPIO_InitStruct.Pin = BNRG_SPI_IRQ_PIN;
    GPIO_InitStruct.Mode = BNRG_SPI_IRQ_MODE;
    GPIO_InitStruct.Pull = BNRG_SPI_IRQ_PULL;
    GPIO_InitStruct.Speed = BNRG_SPI_IRQ_SPEED;
    GPIO_InitStruct.Alternate = BNRG_SPI_IRQ_ALTERNATE;
    HAL_GPIO_Init(BNRG_SPI_IRQ_PORT, &GPIO_InitStruct);
    
    /*##-3- Configure the DMA channel ##########################################*/ 
    static DMA_HandleTypeDef hdma_tx;
    static DMA_HandleTypeDef hdma_rx;
    
    /* Enable DMA2 clock */
    BNRG_DMA_CLK_ENABLE();   
        
    /* Configure the DMA handler for Transmission process */
    hdma_tx.Init.Channel             = BNRG_SPI_TX_DMA_CHANNEL;
    
    hdma_tx.Init.Direction           = DMA_MEMORY_TO_PERIPH;
    hdma_tx.Init.PeriphInc           = DMA_PINC_DISABLE;
    hdma_tx.Init.MemInc              = DMA_MINC_ENABLE;
    hdma_tx.Init.PeriphDataAlignment = DMA_PDATAALIGN_BYTE;
    hdma_tx.Init.MemDataAlignment    = DMA_MDATAALIGN_BYTE;
    hdma_tx.Init.Mode                = DMA_NORMAL;
    hdma_tx.Init.Priority            = DMA_PRIORITY_HIGH;
    hdma_tx.Instance                 = BNRG_SPI_TX_DMA_STREAM;
    
    HAL_DMA_Init(&hdma_tx);   
    peripheral_address = __HAL_BLUENRG_SPI_GET_TX_DATA_REGISTER_ADDRESS(hspi);
    __HAL_BLUENRG_DMA_SET_PERIPHERAL_ADDRESS(&hdma_tx, peripheral_address);
    
    /* Associate the initialized DMA handle to the SPI handle */
    __HAL_LINKDMA(hspi, hdmatx, hdma_tx);
    
    /* Configure the DMA handler for Transmission process */
    hdma_rx.Init.Channel             = BNRG_SPI_RX_DMA_CHANNEL;
    
    hdma_rx.Init.Direction           = DMA_PERIPH_TO_MEMORY;
    hdma_rx.Init.PeriphInc           = DMA_PINC_DISABLE;
    hdma_rx.Init.MemInc              = DMA_MINC_ENABLE;
    hdma_rx.Init.PeriphDataAlignment = DMA_PDATAALIGN_BYTE;
    hdma_rx.Init.MemDataAlignment    = DMA_MDATAALIGN_BYTE;
    hdma_rx.Init.Mode                = DMA_NORMAL;
    hdma_rx.Init.Priority            = DMA_PRIORITY_HIGH;
    hdma_rx.Instance                 = BNRG_SPI_RX_DMA_STREAM;
    
    HAL_DMA_Init(&hdma_rx);
    peripheral_address = __HAL_BLUENRG_SPI_GET_RX_DATA_REGISTER_ADDRESS(hspi);
    __HAL_BLUENRG_DMA_SET_PERIPHERAL_ADDRESS(&hdma_rx, peripheral_address);
    
    /* Associate the initialized DMA handle to the SPI handle */
    __HAL_LINKDMA(hspi, hdmarx, hdma_rx); 
    
    /* Configure the NVIC for SPI */  
    HAL_NVIC_SetPriority(BNRG_SPI_DMA_TX_IRQn, 1, 0);
    HAL_NVIC_EnableIRQ(BNRG_SPI_DMA_TX_IRQn);
    
    HAL_NVIC_SetPriority(BNRG_SPI_DMA_RX_IRQn, 1, 0);
    HAL_NVIC_EnableIRQ(BNRG_SPI_DMA_RX_IRQn);
    
    /* Configure the NVIC for SPI */  
    HAL_NVIC_SetPriority(BNRG_SPI_EXTI_IRQn, 2, 0);    
    //HAL_NVIC_EnableIRQ(BNRG_SPI_EXTI_IRQn);
  }
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
    
/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
