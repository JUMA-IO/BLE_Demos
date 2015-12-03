
/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef _CANON_HAL_H_
#define _CANON_HAL_H_

/* Includes ------------------------------------------------------------------*/
#ifdef USE_STM32F4XX_CANON
  #include "stm32f4xx_hal.h"
  #include "stm32f4xx_canon.h"
  #include "stm32f4xx_canon_bluenrg.h"
  #include "stm32f4xx_hal_conf.h"
#endif

void SystemClock_Config(void);

#endif //_CANON_HAL_H_

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/

